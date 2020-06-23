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

package springfox.documentation.schema
import com.fasterxml.classmate.TypeResolver
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.service.contexts.Defaults

import static springfox.documentation.schema.AlternateTypeRules.*

trait AlternateTypesSupport {

  def defaultRules(TypeResolver resolver = new TypeResolver()) {
    def rules = new Defaults().defaultRules(resolver);
    rules.add(newRule(resolver.arrayType(ToSubstitute), resolver.arrayType(Substituted)))
    rules.add(newRule(resolver.resolve(List, ToSubstitute), resolver.resolve(List, Substituted)))
    rules
  }

  AlternateTypeProvider alternateTypeProvider() {
    new AlternateTypeProvider(defaultRules())
  }

  AlternateTypeProvider alternateRulesWithWildcardMap() {
    def rules = defaultRules()
    rules.add(newMapRule(WildcardType, WildcardType))
    new AlternateTypeProvider(rules)
  }
}
