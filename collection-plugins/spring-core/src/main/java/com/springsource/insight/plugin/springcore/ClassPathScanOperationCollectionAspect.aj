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

package com.springsource.insight.plugin.springcore;

import com.springsource.insight.collection.method.MethodOperationCollectionAspect;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public aspect ClassPathScanOperationCollectionAspect extends MethodOperationCollectionAspect {
    public ClassPathScanOperationCollectionAspect() {
        super();
    }

    public pointcut findPathMatchingResources()
        : execution(* PathMatchingResourcePatternResolver+.findPathMatchingResources(..));

    public pointcut scan()
        : execution(* ClassPathScanningCandidateComponentProvider+.findCandidateComponents(..));

    public pointcut contextRefresh()
        : execution(* ConfigurableApplicationContext+.refresh(..));

    public pointcut collectionPoint() :
        findPathMatchingResources() ||
        scan() ||
        contextRefresh();

    @Override
    public String getPluginName() {
        return SpringCorePluginRuntimeDescriptor.PLUGIN_NAME;
    }
}

