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
package springfox.bean.validators.util;

import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import springfox.bean.validators.plugins.ParameterSizeAnnotationPlugin;
import springfox.documentation.service.AllowableRangeValues;

public class SizeUtil {

     private static final Logger LOG = LoggerFactory.getLogger(ParameterSizeAnnotationPlugin.class);

      public static AllowableRangeValues createAllowableValuesFromSizeForStrings(Size size) {
        LOG.debug("@Size detected: adding MinLength/MaxLength to field");
        return new AllowableRangeValues(minValue(size), maxValue(size));
      }

      private static String minValue(Size size) {
        return String.valueOf(Math.max(size.min(), 0));
      }

      private static String maxValue(Size size) {
        return String.valueOf(Math.max(0, Math.min(size.max(), Integer.MAX_VALUE)));
      }
}
