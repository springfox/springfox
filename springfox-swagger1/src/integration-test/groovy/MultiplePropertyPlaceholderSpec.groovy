/*
 *
 *
 *
 *
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import springfox.documentation.swagger1.configuration.SimpleBean

@WebAppConfiguration
@ContextConfiguration("classpath:placeholders-adapter-context.xml")
class MultiplePropertyPlaceholderSpec extends Specification {

  @Autowired
  WebApplicationContext context

  @Autowired
  SimpleBean simpleBean

  def "should ignore when a property placeholder cannot be resolved"() {
    expect:
      simpleBean.aValue == 'Some Value'
      simpleBean.anotherValue == '${com.yourapp.missingValue}'
  }
}