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

import org.junit.Test;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ObjectMapperConfigurerIntegrationTest {
  @Test
  @SuppressWarnings("unchecked")
  public void objectMapperSetupIsAppliedToAllTransitiveComponentsInRequestMappingHandlerAdapter() {

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);

    context.getBean(RequestMappingHandlerAdapter.class);

    assertThat(TestObjectMapperListener.fired, is(true));
  }

  static class TestObjectMapperListener implements ApplicationListener<ObjectMapperConfigured> {
    static boolean fired = false;
    @Override
    public void onApplicationEvent(ObjectMapperConfigured event) {
      fired = true;
    }
  }

  @Configuration
  static class TestConfig {

    static int numberOfMessageConverters = 0;

    @Bean
    public RequestMappingHandlerAdapter rmh() {
      RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
      numberOfMessageConverters = adapter.getMessageConverters().size();
      return adapter;
    }

    @Bean
    public static ObjectMapperConfigurer objectMapperConfigurer() {
      return new ObjectMapperConfigurer();
    }

    @Bean
    public static TestObjectMapperListener listener() {
      return new TestObjectMapperListener();
    }
  }
}