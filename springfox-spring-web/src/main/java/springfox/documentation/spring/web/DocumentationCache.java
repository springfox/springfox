/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import springfox.documentation.service.Documentation;

import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

public class DocumentationCache {
  private Map<String, Documentation> documentationLookup = newLinkedHashMap();
  private SimpleCacheManager cacheManager = new SimpleCacheManager();

  public DocumentationCache() {
    ConcurrentMapCache operationsCache = new ConcurrentMapCache("operations");
    ConcurrentMapCache modelsCache = new ConcurrentMapCache("models");
    ConcurrentMapCache modelPropertiesCache = new ConcurrentMapCache("modelProperties");
    ConcurrentMapCache modelDependenciesCache = new ConcurrentMapCache("modelDependencies");
    cacheManager.setCaches(newArrayList(operationsCache, modelDependenciesCache, modelsCache, modelPropertiesCache));
    cacheManager.afterPropertiesSet();
  }

  public void addDocumentation(Documentation documentation) {
    documentationLookup.put(documentation.getGroupName(), documentation);
  }

  public Documentation documentationByGroup(String groupName) {
    return documentationLookup.get(groupName);
  }

  public Map<String, Documentation> all() {
    return Collections.unmodifiableMap(documentationLookup);
  }

  public Cache getCache(String cacheName) {
    return cacheManager.getCache(cacheName);
  }

}
