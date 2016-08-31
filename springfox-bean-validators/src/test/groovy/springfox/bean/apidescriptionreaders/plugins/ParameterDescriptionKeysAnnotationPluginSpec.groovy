package springfox.bean.apidescriptionreaders.plugins

import io.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.util.ReflectionUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ValueConstants
import spock.lang.Specification
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.ParameterContext

import java.lang.annotation.Annotation

class ParameterDescriptionKeysAnnotationPluginSpec extends Specification {

    def "Plugin supports everything"() {
        given:
        def plugin = new ParameterDescriptionKeysAnnotationPlugin()
        plugin.propertiesReader = Mock(ApiDescriptionPropertiesReader.class)
        expect:
        supported == plugin.supports(delimiter)
        where:
        supported | delimiter
        true      | DocumentationType.SWAGGER_12
        true      | DocumentationType.SWAGGER_2
        true      | DocumentationType.SPRING_WEB
        true      | new DocumentationType("Everything", "is supported")
        true      | null

    }

    def "Plugin can extract annotation RequestParam from context"() {
        given:
        def plugin = new ParameterDescriptionKeysAnnotationPlugin()

        ParameterContext context = Stub(ParameterContext.class)
        MethodParameter methodParameter = Stub(MethodParameter.class)

        def method = ReflectionUtils.findMethod(AnnotatedMethodParamsHelperClass.class, AnnotatedMethodParamsHelperClass.METHOD_WITH_REQUEST_PARAMETERS, String.class, String.class, String.class)
        Annotation[] annotations = method.getParameterAnnotations()[param]

        Annotation annotation = null;
        if (annotations != null && annotations.length > 0) {
            annotation = annotations[0]
        }

        methodParameter.getParameterAnnotation(RequestParam.class) >> annotation

        context.methodParameter() >> methodParameter

        when:
        def optional = plugin.extractRequestParamAnnotation(context)
        def realValue = null
        Boolean realRequired = null
        def realDefaultValue = null
        if (optional.isPresent()) {
            realValue = optional.get().value()
            realRequired = optional.get().required()
            realDefaultValue = optional.get().defaultValue()
        }

        then:

        containsParam == optional.isPresent()
        value == realValue
        required == realRequired
        defaultValue == realDefaultValue

        where:
        containsParam | param | value                                    | required                                          | defaultValue
        true          | 0     | AnnotatedMethodParamsHelperClass.PARAM_1 | AnnotatedMethodParamsHelperClass.PARAM_1_REQUIRED | AnnotatedMethodParamsHelperClass.PARAM_1_DEFAULT
        true          | 1     | AnnotatedMethodParamsHelperClass.PARAM_2 | true                                              | ValueConstants.DEFAULT_NONE
        false         | 2     | null                                     | null                                              | null
    }

    def "Plugin can extract annotation PathVariable from context"() {
        given:
        def plugin = new ParameterDescriptionKeysAnnotationPlugin()

        ParameterContext context = Stub(ParameterContext.class)
        MethodParameter methodParameter = Stub(MethodParameter.class)
        ParameterBuilder builder = Stub(ParameterBuilder)

        def method = ReflectionUtils.findMethod(AnnotatedMethodParamsHelperClass.class, AnnotatedMethodParamsHelperClass.METHOD_WITH_PATH_VARIABLES, String.class, String.class, String.class)
        Annotation[] annotations = method.getParameterAnnotations()[param]

        Annotation annotation = null;
        if (annotations != null && annotations.length > 0) {
            annotation = annotations[0]
        }

        methodParameter.getParameterAnnotation(PathVariable.class) >> annotation

        context.methodParameter() >> methodParameter

        context.parameterBuilder() >> builder

        when:

        def optional = plugin.extractPathVariableAnnotation(context)
        def realValue = null
        if (optional.isPresent()) {
            realValue = optional.get().value()
        }

        then:

        containsParam == optional.isPresent()
        value == realValue

        where:
        containsParam | param | value
        true          | 0     | AnnotatedMethodParamsHelperClass.PARAM_1
        true          | 1     | AnnotatedMethodParamsHelperClass.PARAM_2
        false         | 2     | null
    }

    def "Plugin can extract annotation ApiParam from context"() {
        given:
        def plugin = new ParameterDescriptionKeysAnnotationPlugin()

        ParameterContext context = Stub(ParameterContext.class)
        MethodParameter methodParameter = Stub(MethodParameter.class)
        ParameterBuilder builder = Stub(ParameterBuilder)

        def method = ReflectionUtils.findMethod(AnnotatedMethodParamsHelperClass.class, AnnotatedMethodParamsHelperClass.METHOD_WITH_API_PARAMETERS, String.class, String.class, String.class)
        Annotation[] annotations = method.getParameterAnnotations()[param]

        Annotation annotation = null;
        if (annotations != null && annotations.length > 0) {
            annotation = annotations[0]
        }

        methodParameter.getParameterAnnotation(ApiParam.class) >> annotation

        context.methodParameter() >> methodParameter

        context.parameterBuilder() >> builder

        when:

        def optional = plugin.extractAnnotation(context)
        def realValue = null
        Boolean realRequired = null
        def realDefaultValue = null
        if (optional.isPresent()) {
            realValue = optional.get().value()
            realRequired = optional.get().required()
            realDefaultValue = optional.get().defaultValue()
        }

        then:

        containsParam == optional.isPresent()
        value == realValue
        required == realRequired
        defaultValue == realDefaultValue

        where:
        containsParam | param | value                                    | required                                          | defaultValue
        true          | 0     | AnnotatedMethodParamsHelperClass.PARAM_1 | AnnotatedMethodParamsHelperClass.PARAM_1_REQUIRED | AnnotatedMethodParamsHelperClass.PARAM_1_DEFAULT
        true          | 1     | AnnotatedMethodParamsHelperClass.PARAM_2 | false                                             | ""
        false         | 2     | null                                     | null                                              | null
    }

    def "Apply plugin"() {
        given:
        def plugin = new ParameterDescriptionKeysAnnotationPlugin()
        def reader = Stub(ApiDescriptionPropertiesReader)
        plugin.propertiesReader = reader

        ParameterContext context = Stub(ParameterContext.class)
        MethodParameter methodParameter = Stub(MethodParameter.class)
        ParameterBuilder builder = Mock(ParameterBuilder.class)

        def method = ReflectionUtils.findMethod(AnnotatedMethodParamsHelperClass.class, AnnotatedMethodParamsHelperClass.METHOD_WITH_API_PARAMETERS, String.class, String.class, String.class)
        Annotation[] annotations = method.getParameterAnnotations()[param]

        Annotation a = null
        if (annotations.length > 0) {
            a = annotations[0]
        }

        methodParameter.getParameterAnnotation(ApiParam.class) >> a

        context.methodParameter() >> methodParameter

        context.parameterBuilder() >> builder

        reader.getProperty(value) >> value

        when:

        plugin.apply(context)

        then:

        invocations * builder.description(value)


        where:
        containsParam | param | invocations | value                                    | required                                          | defaultValue
        true          | 0     | 1           | AnnotatedMethodParamsHelperClass.PARAM_1 | AnnotatedMethodParamsHelperClass.PARAM_1_REQUIRED | AnnotatedMethodParamsHelperClass.PARAM_1_DEFAULT
        true          | 1     | 1           | AnnotatedMethodParamsHelperClass.PARAM_2 | false                                             | ""
        false         | 2     | 0           | null                                     | null                                              | null
    }


}
