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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.types.ResolvedPrimitiveType;

import springfox.documentation.spi.schema.UniqueTypeNameAdjuster;

import static springfox.documentation.schema.Maps.isMapType;
import static springfox.documentation.schema.Types.isBaseType;

public class TypeNameIndexingAdjuster implements UniqueTypeNameAdjuster {
  
  private static final Logger LOG = LoggerFactory.getLogger(TypeNameIndexingAdjuster.class);
  
  private final Map<ResolvedType, Set<Integer>> similarTypes = new HashMap<ResolvedType, Set<Integer>>();
  private final Map<Integer, Integer> modelIdCache = new HashMap<Integer, Integer>();
  private final Map<Integer, Integer> links = new HashMap<Integer, Integer>();

  @Override
  public String get(final int modelId) {
    if (modelIdCache.containsKey(modelId)) {
      Integer index = modelIdCache.get(modelId);
      if (index.equals(0)) {
        return "";
      }
      return String.valueOf(index);
    }
    return "";
  }

  @Override
  public void registerType(final ResolvedType type, final int modelId) {
    if (type instanceof ResolvedPrimitiveType
        || isBaseType(type)
        || isMapType(type)) {
      LOG.debug("Skipping type {} with model id: {}, as a base or primitive type", 
          type, 
          modelId);
      return;
    }
    if (similarTypes.containsKey(type)) {
      similarTypes.get(type).add(modelId);
      build(type);
    } else {
      similarTypes.put(type, new TreeSet<Integer>(Arrays.asList(modelId)));
      modelIdCache.put(modelId, 0);
    }
  }

  @Override
  public void setEqualityFor(final ResolvedType type, final int modelIdOf, final int modelIdTo) {
    if (!similarTypes.containsKey(type)) {
      return;
    }

    Set<Integer> modelIds = similarTypes.get(type);
    if (!modelIds.contains(modelIdOf) || !modelIds.contains(modelIdTo)) {
      return;
    }

    Integer idOf = getOriginal(modelIdOf);
    Integer idTo = getOriginal(modelIdTo);
    if (idOf.equals(idTo)) {
      return;
    }

    links.put(idOf < idTo ? idOf : idTo, idOf < idTo ? idTo : idOf);
    build(type);
  }
  
  private void build(final ResolvedType type) {
    LOG.debug("Rebuilding models indexes for type {}", type);
    Set<Integer> modelIds = similarTypes.get(type);
    int i = 1;
    for(Integer modelId: modelIds) {
      if (links.containsKey(modelId)) {
        LOG.debug("Skipping type with model id: {}, as link to another model {}", 
            modelId, 
            links.get(modelId));
        continue;
      }
      modelIdCache.put(modelId, i);
      ++i;
    }

    for(Integer modelId: modelIds) {
      if (links.containsKey(modelId)) {
        LOG.debug("Adjusting link for model with model id: {}, as link to another model {}", 
            modelId, 
            links.get(modelId));
        Integer link = links.get(modelId);
        link = getOriginal(link);
        modelIdCache.put(modelId, modelIdCache.get(link));
      }
    }
  }
  
  private Integer getOriginal(final int link) {
    int key = link;
    while (links.containsKey(key)) {
      key = links.get(key);
    }
    return key;
  }
}
