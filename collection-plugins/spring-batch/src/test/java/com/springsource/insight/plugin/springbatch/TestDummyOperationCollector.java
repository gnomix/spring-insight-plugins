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

package com.springsource.insight.plugin.springbatch;

import java.util.ArrayList;
import java.util.List;

import com.springsource.insight.collection.OperationCollector;
import com.springsource.insight.intercept.operation.Operation;

/**
 * 
 */
public class TestDummyOperationCollector implements OperationCollector {
    private final List<Operation>   opsList=new ArrayList<Operation>();
    public TestDummyOperationCollector() {
       super();
    }

    List<Operation> getCapturedOperations () {
        return opsList;
    }

    public void enter(Operation operation) {
        opsList.add(operation);
    }

    public void exitNormal() {
        // ignored
    }

    public void exitNormal(Object returnValue) {
        // ignored
    }

    public void exitAbnormal(Throwable throwable) {
        // ignored
    }

    public void exitAndDiscard() {
        // ignored
    }

    public void exitAndDiscard(Object returnValue) {
        // TODO Auto-generated method stub

    }

}
