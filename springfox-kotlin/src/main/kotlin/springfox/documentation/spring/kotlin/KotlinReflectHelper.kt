/*
 *
 *  Copyright 2015-2020 the original author or authors.
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

@file:JvmName("KotlinReflectHelper")

package springfox.documentation.spring.kotlin

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.members.ResolvedMethod
import java.util.*
import java.util.function.Function
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

@JvmOverloads
fun toReturnType(resolver: TypeResolver = TypeResolver()): Function<ResolvedMethod, ResolvedType> = Function { input ->
    val kotlinFunction = input.rawMember.kotlinFunction
    val returnType = when {
        kotlinFunction != null && kotlinFunction.isSuspend -> resolver.resolve(kotlinFunction.returnType.javaType)
        else -> input.returnType
    }

    Optional
        .ofNullable(returnType)
        .orElse(resolver.resolve(Void.TYPE))
}