/**
 * Copyright 2009-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.insight.plugin.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import com.springsource.insight.collection.AbstractOperationCollector;
import com.springsource.insight.collection.OperationCollectionAspectSupport;
import com.springsource.insight.intercept.operation.Operation;


/**
 * 
 */
public class JdbcConnectionCloseOperationCollectionAspectTest
        extends JdbcConnectionOperationCollectionTestSupport {

    public JdbcConnectionCloseOperationCollectionAspectTest() {
        super();
    }

    @Test
    public void testCloseAspect() throws SQLException {
        Connection  conn=connectDriver.connect(connectUrl, connectProps);
        try {
            assertTrackedConnection(conn, connectUrl);
        } finally {
            conn.close();   // don't need it for anything
            assertConnectionNotTracked(conn);
        }
        
        assertCloseDetails(connectUrl);
    }

    @Test
    public void testCloseException() throws Exception {
        Class<?>[]          interfaces={ Connection.class };
        final SQLException  toThrow=new SQLException("testCloseException");
        InvocationHandler   h=new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    if ("close".equals(method.getName())) {
                        throw toThrow;
                    }
                    return null;
                }
            };
        Connection      conn=(Connection) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, h);
        final String    URL="jdbc:test:testCloseException";
        tracker.startTracking(conn, URL);
        try {
            conn.close();
            Assert.fail("Unexpected closure success");
        } catch(SQLException e) {
            Assert.assertSame("Mismatched thrown exception", toThrow, e);
        }
        
        assertCloseDetails(URL);
    }

    @Test
    public void testUntrackedConnectionClose () throws SQLException {
        OperationCollectionAspectSupport    aspectInstance=getAspect();
        final AtomicReference<Operation>    opRef=new AtomicReference<Operation>(null);
        aspectInstance.setCollector(new AbstractOperationCollector() {
                @Override
				protected void enterOperation(Operation operation, Long timestamp) {
                    Operation prev=opRef.getAndSet(operation);
                    Assert.assertNull("Multiple enter calls", prev);
				}

				@Override
				protected void exitOperation(Long timestamp, Object returnValue, boolean validReturn, Throwable throwable) {
					// ignored
				}

				public void exitAndDiscard() {
                    // ignored
                }
    
                public void exitAndDiscard(Object returnValue) {
                    // ignored
                }
            });

        Connection  conn=connectDriver.connect(connectUrl, connectProps);
        for (int    index=0; index < Byte.SIZE; index++) {
            /*
             * NOTE: as per the javadoc:
             * 
             *      "Calling the method close on a connection object that is already closed is a no-op."
             */
            conn.close();

            if (index == 0) {
                assertCloseDetails(opRef.get(), connectUrl);
            }
        }
    }

    @Override
    public JdbcConnectionCloseOperationCollectionAspect getAspect() {
        return JdbcConnectionCloseOperationCollectionAspect.aspectOf();
    }

    private Operation assertCloseDetails (String url) {
        return assertConnectDetails(url, "close");
    }
    
    private Operation assertCloseDetails (Operation op, String url) {
        return assertConnectDetails(op, url, "close");
    }
}
