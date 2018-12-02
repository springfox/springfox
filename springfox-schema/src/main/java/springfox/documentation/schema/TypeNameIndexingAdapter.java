/*
 *
 *  Copyright 2017 the original author or authors.
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

package springfox.documentation.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import springfox.documentation.spi.schema.UniqueTypeNameAdapter;

public class TypeNameIndexingAdapter implements UniqueTypeNameAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(TypeNameIndexingAdapter.class);

  private final Map<String, Map<String, String>> similarTypes = new HashMap<String, Map<String, String>>();
  private final Map<String, String> modelIdCache = new HashMap<String, String>();
  private final Map<String, String> links = new HashMap<String, String>();

  @Override
  public Map<String, String> getLinks() {
    return Collections.unmodifiableMap(links);
  }

  @Override
  public Map<String, String> getSimilarTypes(final String modelId) {
    if (modelIdCache.containsKey(modelId)) {
      return Collections.unmodifiableMap(similarTypes.get(modelIdCache.get(modelId)));
    }
    return new TreeMap<String, String>();
  }

  @Override
  public Optional<String> getTypeName(final String modelId) {
    return Optional.fromNullable(modelIdCache.get(modelId));
  }

  @Override
  public void registerType(final String typeName, final String modelId, final String sortingKey) {
    //CHECKSTYLE:OFF
    System.out.println("Sort key: " + sortingKey);
    //CHECKSTYLE:ON
    if (modelIdCache.containsKey(modelId)) {
      LOG.debug("Rewriting type {} with model id: {}, because it is already registered", 
          typeName, 
          modelId);
      similarTypes.get(modelIdCache.get(modelId)).remove(modelId);
    }
    if (similarTypes.containsKey(typeName)) {
      similarTypes.get(typeName).put(modelId, sortingKey);
    } else {
      Map<String, String> typeRegistration = new TreeMap<String, String>();
      typeRegistration.put(modelId, sortingKey);
      similarTypes.put(typeName, typeRegistration);
    }
    modelIdCache.put(modelId, typeName);
  }

  @Override
  public void setEqualityFor(final String modelIdOf, final String modelIdTo) {
    if (!modelIdCache.containsKey(modelIdOf) ||
        !modelIdCache.containsKey(modelIdTo)) {
      return;
    }
    String id1 = getOriginal(modelIdOf);
    String id2 = getOriginal(modelIdTo);
    if (id1.equals(id2)) {
      return;
    }
    links.put(id1.compareTo(id2) < 0 ? id1 : id2, id1.compareTo(id2) < 0 ? id2 : id1);
  }

  private String getOriginal(final String modelId) {
    String originalId = modelId;
    while (links.containsKey(originalId)) {
      originalId = links.get(originalId);
    }
    return originalId;
  }
}
