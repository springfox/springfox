/**
 * Copyright 2017 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Objects;

/**
 * MapSchema
 */

public class MapSchema extends Schema<Object> {

    private Schema<?> type;

    public MapSchema() {
        super("object", null);
    }

    public MapSchema type(Schema<?> type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (((MapSchema) o).type != this.type) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class MapSchema {\n");
        sb.append("    ").append(this.type.toString()).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
