package springfox.documentation.utils

import spock.lang.Specification
import sun.reflect.annotation.AnnotationParser

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class SpringFoxAnnotationUtilsTest extends Specification {

    def "GetAnnotationsForClassHierarchy"() {

        when:
        def annotations = SpringFoxAnnotationUtils.getAnnotationsForClassHierarchy(C.class, Annotation.class)

        then:
        annotations == [annotation("I1"), annotation("I2"), annotation("C")]

    }

    def annotation(String value) {
        return AnnotationParser.annotationForMap(Annotation.class, [value: value])
    }

}

@Retention(RetentionPolicy.RUNTIME)
@interface Annotation {
    String value()
}

@Annotation(value = "I1")
interface I1 {}

@Annotation(value = "I2")
interface I2 {}

@Annotation(value = "C")
class C implements I1, I2 {}
