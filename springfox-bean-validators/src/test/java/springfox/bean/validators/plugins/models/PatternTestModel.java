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

package springfox.bean.validators.plugins.models;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by igorsokolov on 5/15/16.
 */
public class PatternTestModel {
    private String noAnnotation;

    @Pattern(regexp = "^[A-Z]{3}$")
    private String currencyCode; // three letter iso code

    @Size(min = 1, max = 4)
    @Pattern(regexp = "^[A-Z]{2}$")
    private String countryCode; // two letter iso code
}
