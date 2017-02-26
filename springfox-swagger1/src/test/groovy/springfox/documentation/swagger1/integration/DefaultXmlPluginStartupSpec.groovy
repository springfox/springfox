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

package springfox.documentation.swagger1.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.spring.web.mixins.JsonSupport

@WebAppConfiguration
@ContextConfiguration("classpath:default-plugin-context.xml")
class DefaultXmlPluginStartupSpec extends Specification implements JsonSupport {

  @Autowired
  WebApplicationContext context

  def "Should start app with default xml config"() {
    when:
      MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
      context.publishEvent(new ObjectMapperConfigured(this, new ObjectMapper()))
      MvcResult petApi = mockMvc.perform(MockMvcRequestBuilders.get('/api-docs?group=default')).andReturn()
    then:
      jsonBodyResponse(petApi).apis.size() == 12
  }

}
