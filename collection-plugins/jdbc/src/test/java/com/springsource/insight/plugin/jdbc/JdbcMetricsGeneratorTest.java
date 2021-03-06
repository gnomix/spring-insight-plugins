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

import com.springsource.insight.intercept.metrics.MetricsGenerator;
import com.springsource.insight.intercept.metrics.AbstractMetricsGeneratorTest;
import com.springsource.insight.intercept.operation.OperationType;


public class JdbcMetricsGeneratorTest extends AbstractMetricsGeneratorTest {

	@Override
	protected MetricsGenerator getMetricsGenerator() {
		return new TestingGenerator();
	}

	@Override
	protected OperationType getOperationType() {
		return TestingGenerator.TYPE;
	}

	static class TestingGenerator extends JdbcMetricsGenerator {
	    static final OperationType TYPE=OperationType.valueOf("test-gen");
	    TestingGenerator () {
	        super(TYPE);
	    }
	}
}