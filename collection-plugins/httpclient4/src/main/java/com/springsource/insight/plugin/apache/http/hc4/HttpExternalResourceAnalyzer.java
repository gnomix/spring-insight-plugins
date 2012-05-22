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
package com.springsource.insight.plugin.apache.http.hc4;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.springsource.insight.intercept.color.ColorManager;
import com.springsource.insight.intercept.operation.Operation;
import com.springsource.insight.intercept.operation.OperationFields;
import com.springsource.insight.intercept.operation.OperationList;
import com.springsource.insight.intercept.operation.OperationMap;
import com.springsource.insight.intercept.operation.OperationUtils;
import com.springsource.insight.intercept.topology.ExternalResourceAnalyzer;
import com.springsource.insight.intercept.topology.ExternalResourceDescriptor;
import com.springsource.insight.intercept.topology.ExternalResourceType;
import com.springsource.insight.intercept.topology.MD5NameGenerator;
import com.springsource.insight.intercept.trace.Frame;
import com.springsource.insight.intercept.trace.Trace;
import com.springsource.insight.util.StringUtil;

/**
 * 
 */
public class HttpExternalResourceAnalyzer implements ExternalResourceAnalyzer {
    private final Logger    logger=Logger.getLogger(getClass().getName());
    public HttpExternalResourceAnalyzer() {
        super();
    }

    public Collection<ExternalResourceDescriptor> locateExternalResourceName(Trace trace) {
        Collection<Frame>   framesList=trace.getAllFramesOfType(HttpClientDefinitions.TYPE);
        if ((framesList == null) || framesList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<ExternalResourceDescriptor> resSet=new HashSet<ExternalResourceDescriptor>(framesList.size());
        for (Frame frame : framesList) {
            ExternalResourceDescriptor  res=extractExternalResourceDescriptor(frame);
            if (res == null) {  // can happen if failed to parse the URI somehow
                continue;
            }
            
            if (!resSet.add(res))
                continue;   // debug breakpoint
        }
        
        return resSet;
    }

    ExternalResourceDescriptor extractExternalResourceDescriptor (Frame frame) {
        Operation   op=(frame == null) ? null : frame.getOperation();
        OperationMap requestDetails = (op == null) ? null : op.get("request", OperationMap.class);
        String uriValue = (requestDetails == null) ? null : requestDetails.get(OperationFields.URI, String.class);
        if (StringUtil.getSafeLength(uriValue) <= 0) {
            return null;
        }

        try
        {
            URI uri=new URI(uriValue);
            String name = MD5NameGenerator.getName(uri);
            String host = uri.getHost();
            String server = resolveServerType(op);
            String color = ColorManager.getInstance().getColor(op);
            
            return new ExternalResourceDescriptor(frame, name,
                    ((server == null) ? "" : server + ": ") + host,    // label
                    ExternalResourceType.WEB_SERVER.name(),
                    server,     // vendor
                    host,
                    resolvePort(uri),
                    color);    
        }
        catch(URISyntaxException e)
        {
            logger.warning("Failed to parse " + uriValue + ": " + e.getMessage());
            return null;
        }
    }

    // look for the "Server" response header
    static String resolveServerType (Operation op) {
        OperationMap    responseDetails=(op == null) ? null : op.get("response", OperationMap.class);
        OperationList   responseHeaders=(responseDetails == null) ? null : responseDetails.get("headers", OperationList.class);
        // no headers might be available if not collecting extra information
        int             numHeaders=(responseHeaders == null) ? 0 : responseHeaders.size();
        for (int    index=0; index < numHeaders; index++) {
            OperationMap    nameValuePair=responseHeaders.get(index, OperationMap.class);
            String          name=nameValuePair.get(OperationUtils.NAME_KEY, String.class);
            if ("Server".equalsIgnoreCase(name)) {
                return nameValuePair.get(OperationUtils.VALUE_KEY, String.class);
            }
        }
        
        // this point is reached if no match was found
        return null;
    }

    static int resolvePort (URI uri) {
        if (uri == null) {
            return (-1);
        }

        int port=uri.getPort();
        if (port <= 0) {
            return 80;
        }

        return port;
    }
}
