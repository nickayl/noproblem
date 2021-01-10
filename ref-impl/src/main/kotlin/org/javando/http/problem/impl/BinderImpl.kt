package org.javando.http.problem.impl

import org.javando.http.problem.*

class ProviderBinderImpl : ProviderBinder {
    override val implementation: Class<out JsonProvider?>
        get() = GsonProvider::class.java
}

class ProblemBinderImpl : ProblemBinder {

    override fun getImplementation(provider: JsonProvider): Class<out ProblemBuilder> {
        return ProblemReferenceImplementation.Builder()::class.java
    }
}