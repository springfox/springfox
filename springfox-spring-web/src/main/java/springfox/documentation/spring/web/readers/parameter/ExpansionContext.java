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

package springfox.documentation.spring.web.readers.parameter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.classmate.ResolvedType;

import springfox.documentation.spi.service.contexts.DocumentationContext;

public class ExpansionContext {
    private final String parentName;
    private final ResolvedType paramType;
    private final DocumentationContext documentationContext;
    private final Set<ResolvedType> seenTypes;

    public ExpansionContext(
            String parentName,
            ResolvedType paramType,
            DocumentationContext documentationContext) {
        this(parentName, paramType, documentationContext, new HashSet<>());
    }

    private ExpansionContext(
            String parentName,
            ResolvedType paramType,
            DocumentationContext documentationContext,
            Set<ResolvedType> seenTypes) {
        this.parentName = parentName;
        this.paramType = paramType;
        this.documentationContext = documentationContext;
        this.seenTypes = new HashSet<>(seenTypes);
    }

    public String getParentName() {
        return parentName;
    }

    public ResolvedType getParamType() {
        return paramType;
    }

    public DocumentationContext getDocumentationContext() {
        return documentationContext;
    }

    public boolean hasSeenType(ResolvedType type) {
        return seenTypes.contains(type)
                || Objects.equals(type, paramType);
    }

    public ExpansionContext childContext(
            String parentName,
            ResolvedType paramType,
            DocumentationContext documentationContext) {
        Set<ResolvedType> childSeenTypes = new HashSet<>(seenTypes);
        childSeenTypes.add(paramType);
        return new ExpansionContext(parentName, paramType, documentationContext, childSeenTypes);
    }
}
