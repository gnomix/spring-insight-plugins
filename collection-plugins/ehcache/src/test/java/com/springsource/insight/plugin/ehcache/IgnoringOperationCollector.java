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
package com.springsource.insight.plugin.ehcache;

import com.springsource.insight.collection.AbstractOperationCollector;
import com.springsource.insight.intercept.operation.Operation;

/**
 * TODO move it to insight-collection artifact
 */
public class IgnoringOperationCollector extends AbstractOperationCollector {
    public static final IgnoringOperationCollector  DEFAULT=new IgnoringOperationCollector();
    public IgnoringOperationCollector () {
        super();
    }

    @Override
	protected void enterOperation(Operation operation, Long timestamp) {
        // ignored
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

}
