@file:JvmName("OperationContextHelper")

package springfox.documentation.spring.kotlin

import springfox.documentation.spi.service.contexts.OperationContext


fun OperationContext.isMethodDeprecated(): Boolean = findAnnotation(Deprecated::class.java).isPresent

fun OperationContext.isControllerDeprecated(): Boolean = findControllerAnnotation(Deprecated::class.java).isPresent