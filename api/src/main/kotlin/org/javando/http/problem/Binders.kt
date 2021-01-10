package org.javando.http.problem

interface ProblemBinder {
    fun getImplementation(provider: JsonProvider): Class<out ProblemBuilder>
}

interface ProviderBinder {
    val implementation: Class<out JsonProvider?>
}
