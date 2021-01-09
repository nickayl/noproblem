package org.javando.http.problem.impl

import org.javando.http.problem.JsonProvider
import org.javando.http.problem.ProblemKt
import org.javando.http.problem.ProblemBinder

class ProblemBinderImpl : ProblemBinder {

    override fun getImplementation(provider: JsonProvider): Class<out ProblemKt.Builder> {
        return ProblemReferenceImplementation.Builder(provider)::class.java
    }
}