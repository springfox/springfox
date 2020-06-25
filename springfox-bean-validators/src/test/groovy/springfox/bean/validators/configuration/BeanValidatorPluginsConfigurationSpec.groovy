/*
 *
 *  Copyright 2016-2019 the original author or authors.
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
package springfox.bean.validators.configuration

import spock.lang.Specification
import springfox.bean.validators.plugins.parameter.ExpandedParameterMinMaxAnnotationPlugin
import springfox.bean.validators.plugins.parameter.ExpandedParameterNotNullAnnotationPlugin
import springfox.bean.validators.plugins.parameter.ExpandedParameterPatternAnnotationPlugin
import springfox.bean.validators.plugins.parameter.ExpandedParameterSizeAnnotationPlugin
import springfox.bean.validators.plugins.schema.DecimalMinMaxAnnotationPlugin
import springfox.bean.validators.plugins.schema.IsNullAnnotationPlugin
import springfox.bean.validators.plugins.schema.MinMaxAnnotationPlugin
import springfox.bean.validators.plugins.schema.NotNullAnnotationPlugin
import springfox.bean.validators.plugins.schema.PatternAnnotationPlugin
import springfox.bean.validators.plugins.schema.SizeAnnotationPlugin

class BeanValidatorPluginsConfigurationSpec extends Specification {

    def "Default config match classes"() {
        given:
        def config = new BeanValidatorPluginsConfiguration()
        when:
        def minMaxPlugin = config.minMaxPlugin()
        def isNullPlugin = config.isNullPlugin()
        def notNullPlugin = config.notNullPlugin()
        def patternPlugin = config.patternPlugin()
        def sizePlugin = config.sizePlugin()
        def decimalPlugin = config.decimalMinMaxPlugin()

        def parameterMinMax = config.parameterMinMax()
        def parameterNotNull = config.parameterNotNull()
        def parameterPattern = config.parameterPattern()
        def parameterSize = config.parameterSize()

        def expanderMinMax = config.expanderMinMax()
        def expanderNotNull = config.expanderNotNull()
        def expanderPattern = config.expanderPattern()
        def expanderSize = config.expanderSize()

        then:
        minMaxPlugin instanceof  MinMaxAnnotationPlugin
        isNullPlugin instanceof IsNullAnnotationPlugin
        notNullPlugin instanceof  NotNullAnnotationPlugin
        patternPlugin instanceof PatternAnnotationPlugin
        sizePlugin instanceof  SizeAnnotationPlugin
        decimalPlugin instanceof DecimalMinMaxAnnotationPlugin

        parameterMinMax instanceof springfox.bean.validators.plugins.parameter.MinMaxAnnotationPlugin
        parameterNotNull instanceof springfox.bean.validators.plugins.parameter.NotNullAnnotationPlugin
        parameterPattern instanceof springfox.bean.validators.plugins.parameter.PatternAnnotationPlugin
        parameterSize instanceof springfox.bean.validators.plugins.parameter.SizeAnnotationPlugin

        expanderMinMax instanceof ExpandedParameterMinMaxAnnotationPlugin
        expanderNotNull instanceof ExpandedParameterNotNullAnnotationPlugin
        expanderPattern instanceof ExpandedParameterPatternAnnotationPlugin
        expanderSize instanceof ExpandedParameterSizeAnnotationPlugin


    }
}
