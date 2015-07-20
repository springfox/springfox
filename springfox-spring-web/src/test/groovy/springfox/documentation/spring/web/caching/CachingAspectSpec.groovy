package springfox.documentation.spring.web.caching
import com.fasterxml.classmate.TypeResolver
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.web.method.HandlerMethod
import spock.lang.Specification
import springfox.documentation.annotations.Cacheable
import springfox.documentation.schema.ModelContextKeyGenerator
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.DocumentationCache
import springfox.documentation.spring.web.OperationsKeyGenerator
import springfox.documentation.spring.web.plugins.Docket

class CachingAspectSpec extends Specification {
  def "Coverage for empty pointcut methods" () {
    given:
      CachingAspect sut = new CachingAspect()
    expect:
      sut.operationRead()
      sut.propertiesFor()
      sut.dependenciesFor()
      sut.model()
  }

  def "Operations and properties are cached" () {
    given:
      CachingAspect sut = new CachingAspect()
      sut.cache = new DocumentationCache()
      sut.typeResolver = new TypeResolver()
    and:
      def joinPoint = Mock(ProceedingJoinPoint)
      def proceeded = new Object()
      def methodSignature = Mock(MethodSignature)
      def requestMappingContext = Mock(RequestMappingContext)
      def modelContext = Mock(ModelContext)
      def handlerMethod = Mock(HandlerMethod)
    when:
      handlerMethod.getMethod() >> CachingAspectSpec.methods.find { it.name == "testMethod" }
      modelContext.isReturnType() >> true
      modelContext.resolvedType(_) >> sut.typeResolver.resolve(String)
      requestMappingContext.getHandlerMethod() >> handlerMethod
      requestMappingContext.getDocumentationContext() >> new Docket(DocumentationType.SPRING_WEB).configure(new
          DocumentationContextBuilder(DocumentationType.SPRING_WEB))
      joinPoint.getSignature() >> methodSignature
      joinPoint.getArgs() >> [requestMappingContext, modelContext]
      methodSignature.getMethod() >> CachingAspectSpec.methods.find { it.name == "testMethod" }
      joinPoint.proceed() >> proceeded
    and:
      def aspectAdjusted = sut."$methodName"(joinPoint,
          [value: { -> "models"}, keyGenerator: {-> keyGenerator}] as Cacheable)
    then:
      proceeded == aspectAdjusted
    where:
      keyGenerator            | methodName                  | cachedValue
      OperationsKeyGenerator  | "operationsAndProperties"   | new Object()
      OperationsKeyGenerator  | "operationsAndProperties"   | null
      ModelContextKeyGenerator| "modelsAndDependencies"     | new Object()
      ModelContextKeyGenerator| "modelsAndDependencies"     | null


  }

  def testMethod(RequestMappingContext request, ModelContext model) {
  }

}
