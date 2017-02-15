/*
 *
 *  Copyright 2017 the original author or authors.
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
package springfox.documentation.spring.web.dummy.models;

import java.util.List;

public class SomeType {
    private String string1;
    private OtherType otherType;
    private List<SomeType> someOtherTypes;

    public List<SomeType> getSomeOtherTypes() {
        return someOtherTypes;
    }

    public void setSomeOtherTypes(List<SomeType> someOtherTypes) {
        this.someOtherTypes = someOtherTypes;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public OtherType getOtherType() {
        return otherType;
    }

    public void setOtherType(final OtherType otherType) {
        this.otherType = otherType;
    }
}
