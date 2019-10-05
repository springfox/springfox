package springfox.documentation.spi.service.contexts

import spock.lang.Specification
import springfox.documentation.RequestHandler
import springfox.documentation.RequestHandlerKey
import springfox.documentation.service.ResolvedMethodParameter
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import springfox.documentation.spring.wrapper.PatternsRequestCondition
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.mixins.HandlerMethodsSupport
import springfox.documentation.spring.web.dummy.models.SameFancyPet
import springfox.documentation.spring.web.ControllerNamingUtils
import springfox.documentation.spring.web.dummy.controllers.GenericRestController;
import springfox.documentation.spring.web.dummy.controllers.PetRepository;

import static java.util.stream.Collectors.*

import java.lang.annotation.Annotation

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.NameValueExpression
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

class OrderingsSpec extends Specification {

  def methodResolver = new HandlerMethodResolver(new TypeResolver())

  def "Orderings dont crash when docket group names are null" () {
    given:
      Docket docket1 = new Docket(DocumentationType.SPRING_WEB)
      Docket docket2 = new Docket(DocumentationType.SWAGGER_12).groupName("non-default")
      Docket docket3 = new Docket(DocumentationType.SWAGGER_12).groupName("a#1")
    when:
      def ordered = [docket1, docket2, docket3].stream().sorted(Orderings.byPluginName()).collect(toList())
    then:
      ordered[0] == docket3
      ordered[1] == docket1
      ordered[2] == docket2
  }

  def "Orderings is stable when ResourceGroup is based on empty class" () {
    given:
      ResourceGroup group1 =  new ResourceGroup("test", PetRepository, 0)
      ResourceGroup group2 =  new ResourceGroup("test", GenericRestController, 0)
      ResourceGroup group3 =  new ResourceGroup("test", null, 0)

    when:
      def ordered = [group1, group2, group3].stream().sorted(Orderings.resourceGroupComparator()).collect(toList())
    then:
      ordered[0] == group3
      ordered[1] == group2
      ordered[2] == group1
  }

  def "Orderings is stable when RequestMappingContext is based on overloaded methods" () {
    given:
      DocumentationContext documentationContext = Mock()

      RequestMappingContext context1 =  requestMappingContext("0", documentationContext, SameFancyPet)
      RequestMappingContext context2 =  requestMappingContext("0", documentationContext, SameFancyPet, String)
      RequestMappingContext context3 =  requestMappingContext("0", documentationContext, String)

    when:
      def ordered = [context1, context2, context3].stream().sorted(Orderings.methodComparator()).collect(toList())
    then:
      ordered[0] == context3
      ordered[1] == context2
      ordered[2] == context1
  }

  private RequestMappingContext requestMappingContext(String id, DocumentationContext documentationContext, Class<?> ... params) {
    new RequestMappingContext(
        id,
        documentationContext,
        requestHandler(methodResolver, handlerMethod(params)))
  }

  private HandlerMethod handlerMethod(Class<?> ... params) {
    def clazz = new DummyClass()
    Class c = clazz.getClass();
    new HandlerMethod(clazz, c.getMethod("methodToTestOrdering", params))
  }

  def requestHandler(HandlerMethodResolver methodResolver, HandlerMethod handlerMethod) {
    new RequestHandler() {
          @Override
          Class<?> declaringClass() {
            return null
          }

          @Override
          boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
            return false
          }

          @Override
          PatternsRequestCondition getPatternsCondition() {
            return null
          }

          @Override
          String groupName() {
            return ControllerNamingUtils.controllerNameAsGroup(handlerMethod);
          }

          @Override
          String getName() {
            return handlerMethod.getMethod().getName();
          }

          @Override
          Set<RequestMethod> supportedMethods() {
            return null
          }

          @Override
          Set<? extends MediaType> produces() {
            return null
          }

          @Override
          Set<? extends MediaType> consumes() {
            return null
          }

          @Override
          Set<NameValueExpression<String>> headers() {
            return null
          }

          @Override
          Set<NameValueExpression<String>> params() {
            return null
          }

          @Override
          def <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
            return null
          }

          RequestHandlerKey key() {
            handlerKey == null ? null : new RequestHandlerKey([] as Set, [] as Set, [] as Set, [] as Set)
          }

          @Override
          List<ResolvedMethodParameter> getParameters() {
            return methodResolver.methodParameters(handlerMethod);
          }

          @Override
          ResolvedType getReturnType() {
            return methodResolver.methodReturnType(handlerMethod);
          }

          @Override
          def <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
            return null
          }

          @Override
          springfox.documentation.spring.wrapper.RequestMappingInfo getRequestMapping() {
            return null
          }

          @Override
          HandlerMethod getHandlerMethod() {
            return null
          }

          @Override
          RequestHandler combine(RequestHandler other) {
            return null
          }
        }
  }
}