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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import springfox.documentation.spi.schema.UniqueTypeNameAdapter;

public class TypeNameIndexingAdapter implements UniqueTypeNameAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(TypeNameIndexingAdapter.class);

  private final Map<String, Set<Integer>> similarTypes = new HashMap<String, Set<Integer>>();
  private final Map<Integer, String> modelIdCache = new HashMap<Integer, String>();
  private final Map<Integer, Integer> links = new HashMap<Integer, Integer>();

  @Override
  public Map<Integer, Integer> getLinks() {
    return Collections.unmodifiableMap(links);
  }

  @Override
  public Set<Integer> getSimilarTypes(final int modelId) {
    if (modelIdCache.containsKey(modelId)) {
      return Collections.unmodifiableSet(similarTypes.get(modelIdCache.get(modelId)));
    }
    return new TreeSet<Integer>();
  }

  @Override
  public Optional<String> getTypeName(final int modelId) {
    return Optional.fromNullable(modelIdCache.get(modelId));
  }

  @Override
  public void registerType(final String typeName, final int modelId) {
    if (modelIdCache.containsKey(modelId)) {
      LOG.debug("Rewriting type {} with model id: {}, because it is already registered", 
          typeName, 
          modelId);
      similarTypes.get(modelIdCache.get(modelId)).remove(modelId);
    }
    if (similarTypes.containsKey(typeName)) {
      similarTypes.get(typeName).add(modelId);
    } else {
      similarTypes.put(typeName, new TreeSet<Integer>(Arrays.asList(modelId)));
    }
    modelIdCache.put(modelId, typeName);
  }

  @Override
  public void setEqualityFor(final int modelIdOf, final int modelIdTo) {
    if (!modelIdCache.containsKey(modelIdOf) ||
        !modelIdCache.containsKey(modelIdTo) ||
        modelIdOf == modelIdTo) {
      return;
    }

    links.put(modelIdOf < modelIdTo ?
        modelIdOf : modelIdTo, modelIdOf < modelIdTo ? modelIdTo : modelIdOf);
  }
}
