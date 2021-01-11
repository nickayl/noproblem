package org.javando.http.problem

import java.lang.Exception
import java.util.*

private var binder: ProblemBinder? = null
internal fun findProblemBuilderOrThrow(provider: JsonProvider): ProblemBuilder {
    return try {
        binder = Optional.ofNullable(binder)
            .orElse(
                Class.forName("org.javando.http.problem.impl.ProblemBinderImpl")
                    .getDeclaredConstructor()
                    .newInstance() as ProblemBinder
            )
        binder!!.getImplementation(provider)
            .getDeclaredConstructor()
            .newInstance()
    } catch (e: Exception) {
        e.printStackTrace()
        throw MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e)
    }
}

private var defaultProvider: JsonProvider? = null
internal fun getDefaultProviderOrThrow(): JsonProvider {
    return defaultProvider ?: try {
        val binder = Class
            .forName("org.javando.http.problem.impl.ProviderBinderImpl")
            .getDeclaredConstructor()
            .newInstance() as ProviderBinder
        binder.implementation
            .getDeclaredConstructor()
            .newInstance()
    } catch (e: Exception) {
        e.printStackTrace()
        throw MissingImplementationException("Error instantiating the default JsonProvider: Cannot find a valid implementation for ProviderBinderImpl", e)
    }!!
}