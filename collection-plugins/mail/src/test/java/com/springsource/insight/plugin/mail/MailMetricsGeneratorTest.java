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
package com.springsource.insight.plugin.mail;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.springsource.insight.intercept.metrics.AbstractMetricsGenerator;
import com.springsource.insight.intercept.metrics.AbstractMetricsGeneratorTest;
import com.springsource.insight.intercept.metrics.MetricsBag;
import com.springsource.insight.intercept.metrics.MetricsGenerator;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationType;
import com.springsource.insight.intercept.resource.ResourceKey;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.FrameId;
import com.springsource.insight.intercept.trace.SimpleFrame;
import com.springsource.insight.util.IDataPoint;

public class MailMetricsGeneratorTest extends AbstractMetricsGeneratorTest {

	@Override
	protected MetricsGenerator getMetricsGenerator() {
		return new MailMetricsGenerator();
	}

	@Override
	protected OperationType getOperationType(){
		return MailDefinitions.SEND_OPERATION;
	}
    
	@Override
	protected void validateMetricsBags(List<MetricsBag> mbs, MetricsGenerator gen) {
		assertEquals(2, mbs.size());
        
        MetricsBag mb = mbs.get(0);

        List<String> keys = mb.getMetricKeys();
        assertEquals(3, keys.size());
        
        assertEquals("opExtKey", mb.getResourceKey().getName());

        List<IDataPoint> points = mb.getPoints(AbstractMetricsGenerator.EXECUTION_TIME);
        assertEquals(1, points.size());
        assertEquals(160.0 , points.get(0).getValue(), .0001);

        points = mb.getPoints(AbstractMetricsGenerator.INVOCATION_COUNT);
        assertEquals(1, points.size());
        assertEquals(1.0, points.get(0).getValue(), .0001);
        
        points = mb.getPoints(MailMetricsGenerator.MAIL_SIZE_METRIC);
        assertEquals(1, points.size());
        assertEquals(256.0, points.get(0).getValue(), 0.01);
	}
	
	@Override
	 protected List<Frame> makeFrame() {
	        Operation op = new Operation().type(getOperationType());
	        op.put(ResourceKey.OPERATION_KEY, "insight:name=\"opExtKey\",type=EndpointApplicationServerExternalResource");
	        op.put("size", 256);
	        List<Frame> res = new ArrayList<Frame>();
	        res.add(new SimpleFrame(FrameId.valueOf(1), null, op, timeRange, Collections.<Frame>emptyList()));
	        res.add(new SimpleFrame(FrameId.valueOf(1), null, op, timeRange, Collections.<Frame>emptyList()));
	        return res;
	    }
}