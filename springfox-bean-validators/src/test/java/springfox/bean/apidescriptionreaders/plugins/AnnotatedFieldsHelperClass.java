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

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.*;
import java.util.Date;

public class AnnotatedFieldsHelperClass {

    public static final String FIELD_ASSERT_FALSE = "assertFalse";
    public static final String FIELD_ASSERT_TRUE = "assertTrue";
    public static final String FIELD_DECIMAL_MAX = "decimalMax";
    public static final String FIELD_DECIMAL_MIN = "decimalMin";
    public static final String FIELD_DIGITS = "digits";
    public static final String FIELD_FUTURE = "future";
    public static final String FIELD_MAX = "max";
    public static final String FIELD_MIN = "min";
    public static final String FIELD_NOT_NULL = "notNull";
    public static final String FIELD_NULL = "nullField";
    public static final String FIELD_PAST = "past";
    public static final String FIELD_PATTERN = "pattern";
    public static final String FIELD_SIZE = "size";

    @AssertFalse
    @ApiParam
    private Boolean assertFalse;

    @AssertTrue
    @ApiParam(value = FIELD_ASSERT_FALSE)
    private Boolean assertTrue;

    @DecimalMax(value = "1")
    @ApiParam(value = FIELD_ASSERT_TRUE)
    private Double decimalMax;

    @DecimalMin(value = "1")
    @ApiModelProperty
    private Double decimalMin;

    @Digits(integer = 1, fraction = 0)
    @ApiModelProperty(value = FIELD_DIGITS)
    private Double digits;

    @Future
    @ApiModelProperty(value = FIELD_PAST)
    private Date future;

    @Max(value = 1)
    private Integer max;

    @Min(value = 1)
    private Integer min;

    @NotNull
    private String notNull;

    @Null
    private String nullField;

    @Past
    private Date past;

    @Pattern(regexp = "")
    private String pattern;

    @Size
    private String size;


}
