package org.javando.http.problem.impl

import org.javando.http.problem.JsonProvider
import org.javando.http.problem.ProblemKt
import org.javando.http.problem.ProblemBinder
import org.javando.http.problem.ProviderBinder

class ProviderBinderImpl : ProviderBinder {

    override fun getImplementation(): Class<out JsonProvider> {
        return GsonProvider::class.java
    }
}