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

package springfox.documentation.spring.web.mixins
import com.fasterxml.classmate.MemberResolver
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedMethod
import org.springframework.web.method.HandlerMethod
import springfox.documentation.spring.web.dummy.DummyClass

trait HandlerMethodsSupport {
  HandlerMethod methodWithChild() {
    def clazz = new DummyClass.MethodsWithSameName()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("methodToTest", Integer, DummyClass.Child))
  }

  HandlerMethod loadDetailsWithOneParameter() {
    def clazz = new DummyClass.MethodResolutionToDemonstrate1241()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("loadDetails", String))
  }

  HandlerMethod loadDetailsWithTwoParameter() {
    def clazz = new DummyClass.MethodResolutionToDemonstrate1241()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("loadDetails", String, Date))
  }

  HandlerMethod unresolvableMethod() {
   null
  }

  ResolvedMethod resolvedMethod() {
    def typeResolver = new TypeResolver()
    ResolvedType dummy = typeResolver.resolve(DummyClass)
    def memberResolver = new MemberResolver(typeResolver)
    def resolvedMembers = memberResolver.resolve(dummy, null, null)
    return resolvedMembers.getMemberMethods().find { "methodThatIsHidden".equals(it.getName())}
  }

  HandlerMethod methodWithParent() {
    def clazz = new DummyClass.MethodsWithSameName()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("methodToTest", Integer, DummyClass.Parent))
  }

  HandlerMethod methodOnDummyClass(String method, Class ... params) {
    def clazz = new DummyClass()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod(method, params))
  }
}
