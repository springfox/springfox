package springfox.bean.validators.configuration

import spock.lang.Specification
import springfox.bean.validators.plugins.ModelPropertyMinMaxAnnotationPlugin
import springfox.bean.validators.plugins.ModelPropertyNotNullAnnotationPlugin
import springfox.bean.validators.plugins.ModelPropertySizeAnnotationPlugin

class BeanValidatorPluginsConfigurationSpec extends Specification {

    def "Defautl coonfig match classes"() {
        given:
        def config = new BeanValidatorPluginsConfiguration()
        when:
        def clazz1 = config.minMaxPlugin()
        def clazz2 = config.notNullPlugin()
        def clazz3 = config.sizePlugin()
        then:
        clazz1 instanceof  ModelPropertyMinMaxAnnotationPlugin
        clazz2 instanceof  ModelPropertyNotNullAnnotationPlugin
        clazz3 instanceof  ModelPropertySizeAnnotationPlugin
    }
}
