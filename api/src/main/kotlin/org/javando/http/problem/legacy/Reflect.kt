package org.javando.http.problem.legacy

import java.util.*
import kotlin.Exception

//private var binder: ProblemBinder? = null
//internal fun getProblemImplOrDefault(provider: JsonProvider): ProblemBuilder {
//    return try {
//        binder = Optional.ofNullable(binder)
//            .orElse(
//                Class.forName("org.javando.http.problem.impl.ProblemBinderImpl")
//                    .getDeclaredConstructor()
//                    .newInstance() as ProblemBinder)
//        binder!!.getImplementation(provider)
//            .getDeclaredConstructor(JsonProvider::class.java)
//            .newInstance(provider)
//    } catch (e: ClassNotFoundException) {
//        //e.printStackTrace()
//        return ProblemReferenceImplementation.Builder(provider)
//    } catch (e: NoSuchMethodException) {
//        e.printStackTrace();
//        throw MalformedImplementationException("Cannot instantiate a ProblemBuilder instance due to a constructor not compliant with the api.", e)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        throw MissingImplementationException("Cannot find a valid implementation of ProblemBinder", e)
//    }
//}
//
//private var defaultProvider: JsonProvider? = null
//internal fun getDefaultProviderOrThrow(): JsonProvider {
//    return defaultProvider ?: try {
//        val binder = Class
//            .forName("org.javando.http.problem.impl.ProviderBinderImpl")
//            .getDeclaredConstructor()
//            .newInstance() as ProviderBinder
//        binder.implementation
//            .getDeclaredConstructor()
//            .newInstance()
//    } catch (e: Exception) {
//        e.printStackTrace()
//        throw MissingImplementationException("Error instantiating the default JsonProvider: Cannot find a valid implementation for ProviderBinderImpl", e)
//    }!!
//}