package springfox.documentation.spring.web.configuration;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiParam;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Pageable;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
public class SpringPageableConfiguration {

    // tag::annotation-holder[]
    @ApiParam(value = "Results page you want to retrieve (0..N).", example = "0")
    private String page = "";

    @ApiParam(value = "Number of records per page.", example = "20")
    private String limit = "";

    @ApiParam(value = "Sorting criteria in the format: property(,asc|desc).", example = "id,asc")
    private String sort = "";
    // tag::annotation-holder[]

    // tag::alternate-type-rule-convention[]
    @Bean
    public AlternateTypeRuleConvention pageableConvention(final TypeResolver resolver) {
        return new AlternateTypeRuleConvention() {

            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public List<AlternateTypeRule> rules() {
                return Collections.singletonList(
                        newRule(resolver.resolve(Pageable.class), resolver.resolve(pageableMixin())));
            }
        };
    }
    // tag::alternate-type-rule-convention[]

    // tag::alternate-type-builder[]
    private Type pageableMixin() {

        try {
            Annotation pageAnnotation = getApiParamAnnotation("page");
            Annotation limitAnnotation = getApiParamAnnotation("limit");
            Annotation sortAnnotation = getApiParamAnnotation("sort");
            return new AlternateTypeBuilder()
                    .fullyQualifiedClassName(String.format("%s.generated.%s", Pageable.class.getPackage().getName(),
                            Pageable.class.getSimpleName()))
                    .withProperties(Arrays.asList(
                            property(Integer.class, "page", pageAnnotation),
                            property(Integer.class, "limit", limitAnnotation),
                            property(String[].class, "sort", sortAnnotation)))
                    .build();
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Can't create Pageable convention");
        }
    }

    private ApiParam getApiParamAnnotation(String fieldName) throws NoSuchFieldException {
        return this.getClass().getDeclaredField(fieldName).getAnnotation(ApiParam.class);
    }

    private AlternateTypePropertyBuilder property(Class<?> type, String name, Annotation annotation) {
        return new AlternateTypePropertyBuilder()
                .withName(name)
                .withType(type)
                .withCanRead(true)
                .withCanWrite(true)
                .withAnnotations(Collections.singletonList(annotation));
    }
    // tag::alternate-type-builder[]
}
