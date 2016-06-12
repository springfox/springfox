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
package springfox.bean.apidescriptionreaders.plugins;

import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public class AnnotatedMethodParamsHelperClass {

    public static final String METHOD_WITH_REQUEST_PARAMETERS = "methodWithRequestParameters";
    public static final String METHOD_WITH_API_PARAMETERS = "methodWithApiParameters";
    public static final String METHOD_WITH_PATH_VARIABLES = "methodWithPathVariable";
    public static final String PARAM_1 = "param1";
    public static final String PARAM_1_DEFAULT = "";
    public static final boolean PARAM_1_REQUIRED = false;
    public static final String PARAM_2 = "myParam2";

    public void methodWithRequestParameters(
            @RequestParam(value = PARAM_1, required = PARAM_1_REQUIRED, defaultValue = PARAM_1_DEFAULT) String param1,
            @RequestParam(value = PARAM_2) String param2,
            String param3) {
    }

    public void methodWithApiParameters(
            @ApiParam(value = PARAM_1, required = PARAM_1_REQUIRED, defaultValue = PARAM_1_DEFAULT) String param1,
            @ApiParam(value = PARAM_2) String param2,
            String param3) {
    }

    public void methodWithPathVariable(
            @PathVariable(value = PARAM_1) String param1,
            @PathVariable(value = PARAM_2) String param2,
            String param3) {
    }
}
