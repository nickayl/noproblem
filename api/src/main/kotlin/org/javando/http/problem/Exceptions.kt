package org.javando.http.problem

class ProblemBuilderException(
    override val message: String?,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

open class JsonParseException(
    override val message: String?,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)

class InvalidJsonStringException(
    override val message: String?,
    override val cause: Throwable? = null
) : JsonParseException(message, cause)

class InvalidJsonValueException(
    override val message: String?,
    override val cause: Throwable? = null
) : JsonParseException(message, cause)

class MissingRequiredMemberException (
    override val message: String?,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)




//class JsonProviderNotSetException @JvmOverloads constructor(
//    override val message: String?,
//    override val cause: Throwable? = null
//) : RuntimeException(message, cause)

//
//class MissingImplementationException : RuntimeException {
//    constructor(message: String?) : super(message) {}
//    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
//}
//
//class MalformedImplementationException : RuntimeException {
//    constructor(message: String?) : super(message) {}
//    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
//}
