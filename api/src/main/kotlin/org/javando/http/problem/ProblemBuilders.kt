package org.javando.http.problem

import java.net.URI

abstract class ProblemBuilderWither(jsonProvider: JsonProvider) : ProblemBuilder(jsonProvider) {

    open fun withTitle(title: String): ProblemBuilderWither {
        title(title)
        return this
    }

    open fun withDetails(details: String): ProblemBuilderWither {
        details(details)
        return this
    }

    open fun withType(uri: URI): ProblemBuilderWither {
        type(uri)
        return this
    }

    open fun withStatus(status: HttpStatus): ProblemBuilderWither {
        status(status)
        return this;
    }

    open fun withInstance(uri: URI): ProblemBuilderWither {
        instance(uri)
        return this
    }
}

abstract class ProblemBuilderClassic(jsonProvider: JsonProvider) : ProblemBuilder(jsonProvider) {

    public override fun title(title: String): ProblemBuilderClassic {
        super.title(title)
        return this
    }

    public override fun details(details: String): ProblemBuilderClassic {
        super.details(details)
        return this
    }

    public override fun type(uri: URI): ProblemBuilderClassic {
        super.type(uri)
        return this
    }

    public override fun status(status: HttpStatus): ProblemBuilderClassic {
        super.status(status)
        return this
    }

    public override fun instance(uri: URI): ProblemBuilderClassic {
        super.instance(uri)
        return this
    }

}
