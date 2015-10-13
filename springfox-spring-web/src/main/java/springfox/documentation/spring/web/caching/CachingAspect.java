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
package springfox.documentation.spring.web.caching;

import com.fasterxml.classmate.TypeResolver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import springfox.documentation.annotations.Cacheable;
import springfox.documentation.spring.web.DocumentationCache;

import java.lang.reflect.Method;

@Aspect
@Component
public class CachingAspect {
  private static final Logger LOG = LoggerFactory.getLogger(CachingAspect.class);
  @Autowired
  private DocumentationCache cache;
  @Autowired
  private TypeResolver typeResolver;

  @Pointcut("execution(* springfox.documentation.spring.web.readers.operation.ApiOperationReader.read(..))")
  public void operationRead() {
  }

  @Pointcut("execution(* springfox.documentation.schema.property.ModelPropertiesProvider+.propertiesFor(..))")
  public void propertiesFor() {
  }

  @Pointcut("execution(* springfox.documentation.schema.DefaultModelDependencyProvider.dependentModels(..))")
  public void dependenciesFor() {
  }

  @Pointcut("execution(* springfox.documentation.schema.ModelProvider+.modelFor(..))")
  public void model() {
  }

  @Around("(operationRead() || propertiesFor()) && @annotation(cacheable)")
  public Object operationsAndProperties(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    KeyGenerator keyGenerator = cacheable.keyGenerator().newInstance();
    Object key = keyGenerator.generate(joinPoint.getTarget(), method, joinPoint.getArgs());
    LOG.info("Caching aspect applied for cache {} with key {}", cacheable.value(), key);
    return cachedValue(joinPoint, cacheable.value(), key);
  }

  @Around("(model() || dependenciesFor()) && @annotation(cacheable)")
  public Object modelsAndDependencies(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {

    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    KeyGenerator keyGenerator = cacheable.keyGenerator().getDeclaredConstructor(TypeResolver.class)
        .newInstance(typeResolver);
    Object key = keyGenerator.generate(joinPoint.getTarget(), method, joinPoint.getArgs());
    LOG.info("Caching aspect applied for cache {} with key {}", cacheable.value(), key);
    return cachedValue(joinPoint, cacheable.value(), key);
  }

  private Object cachedValue(ProceedingJoinPoint joinPoint, String cacheName, Object key) throws Throwable {
    Cache cache = this.cache.getCache(cacheName);
    if (cache.get(key) == null) {
      cache.put(key, joinPoint.proceed());
    }
    return cache.get(key).get();
  }
}
