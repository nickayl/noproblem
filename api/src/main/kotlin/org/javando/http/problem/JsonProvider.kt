package org.javando.http.problem;

import java.util.*
import java.util.regex.Pattern
import kotlin.reflect.KClass

/**
 *  The provider is the engine that performs the serialization/deserialization functions. The default provider is GsonProvider.
 */
interface JsonProvider {

    /**
     * Serialize the problem in a JSON Object that conforms to the [RFC 7807](https://tools.ietf.org/html/rfc7807) specification.
     *
     * @param problem the problem instance to be serialized
     * @return The serialized problem in a JSON Object string
     */
    fun toJson(problem: Problem): String


    /**
     * Helper function useful if you want to serialize custom or independent JSON values.
     *
     * @param element the [JsonValue] to be serialized in its equivalent JSON String format.
     * @return The serialized JSON string value
     */
    fun toJson(element: JsonValue): String

    /**
     * Serialize the given problem into a provider-independent JSON Object.
     *
     * See [JsonObject] for more information on how to extract json properties from this object
     * @param problem The problem to be serialized
     * @return The [JsonObject] instance
     */
    fun toJsonObject(problem: Problem): JsonObject

    /**
     * Deserialize the given JSON string into a [Problem] instance.
     *
     * @param str The JSON string representing a JSON Object compliant with the [RFC 7807](https://tools.ietf.org/html/rfc7807)  specification
     * @return The deserialized [Problem] instance.
     * @throws InvalidJsonStringException if the string is not a valid JSON object or if the string is not compliant with the [RFC 7807][https://tools.ietf.org/html/rfc7807]
     */
    fun fromJson(str: String): Problem


    /**
     * Get the actual JSON provider that will do the serialization and deserialization job.
     * If you have the 'gson-provider' dependency, this is equivalent to a [com.google.gson.Gson] instance.
     *
     * @returns the json provider instance used internally.
     */
    val get: Any


    fun registerExtensionClasses(vararg pairs: Pair<String, Class<*>>): JsonProvider

    /**
     * You can register your custom classes so they can be deserialized in the correct class instance.
     * Note that the parameter name must be in snake_case and have to correspond with the class name in PascalCase.
     * Consider this example:
     *
     * ``` kotlin
     * // suppose you have this custom class...
     * data class CreditInfo(val balance: Double, val currency: String = "EUR")
     *
     *  // Then you can register it in this way:
     *      val provider = GsonProvider()
     * // Note the "credit_info" in 'snake_case' name correspond with 'CreditInfo' class name in PascalCase
     *      provider.registerExtensionClass("credit_info", CreditInfo::class.java)
     *
     * val p = Problem.wither(provider)
     *  .withType(URI("https://www.myapi.com/errors/insufficient-credit.html"))
     *  .withInstance(URI("/perform-transaction"))
     *  .withTitle("Insufficient Credit")
     *  .withDetails("There's no sufficient credit in the account for the requested transaction")
     *  .withStatus(HttpStatus.FORBIDDEN)
     *  .addExtension("account_number", 7699123)
     *  .addExtension("transaction_id", "f23a7600ffd6")
     *  .addExtension("transaction_date", Date())
     *  .addExtension("credit_info", CreditInfo(34.5f, "EUR"))
     *  .build()
     *
     *  // Will produce this output:
     *
     *  {
     *   "type":"https://www.myapi.com/errors/insufficient-credit.html",
     *   "title":"Insufficient Credit",
     *   "details":"There's no sufficient credit in the account for the requested transaction",
     *   "status":401,
     *   "instance":"/perform-transaction",
     *   "account_number":7699123,
     *   "transaction_id":"f23a7600ffd6",
     *   "transaction_date":"13/01/2021 05:52:20",
     *   "credit_info":{
     *          "balance":34.5,
     *          "currency":"EUR"
     *      }
     *   }
     * ```
     *  When deserializing the same string, you can get back your class
     *  instance with the [Problem.getExtensionValue] method:
     *
     *  ```
     *  val problem = Problem.from(string, provider)
     *  val creditInfo: CreditInfo = problem.getExtensionValue("credit_info", CreditInfo::class.java)
     *
     *  // or
     *
     * val creditInfo = problem.getExtensionValue(CreditInfo::class.java)
     *
     * // In Java:
     *
     * CreditInfo creditInfo = problem.getExtensionValue(CreditInfo.class)
     * ```
     *
     * @param jsonPropertyName The json property name. It must be the same as the class name, but in snake_case.
     * @return the same [JsonProvider] instance to allow chaining.
     */
    fun registerExtensionClass(jsonPropertyName: String, klass: Class<*>): JsonProvider

    /**
     * Same as [registerExtensionClass(String, Class<*>)] but the PascalCase to snake_case conversion is done automatically.
     * See the other overloaded [registerExtensionClass] method for a detailed explanation.
     */
    fun registerExtensionClass(klass: Class<*>): JsonProvider


    fun removeExtensionClass(jsonPropertyName: String): JsonProvider

    /**
     *
     * @param pattern The date pattern that will be used to deserialize date properties.
     * The pattern is the same used by [java.text.DateFormat] and its implementations.
     * @return the same [JsonProvider] instance to allow chaining.
     */
    fun setDateFormat(pattern: String): JsonProvider

    /**
     *
     * @param identifier The string used to identify json property names as Date object.
     * Note that this is only an hint given to the library, if the identified date property cannot be deserialized into a Date object,
     * a normal [JsonString] will be given instead of [JsonDate]
     * @return the same [JsonProvider] instance to allow chaining.
     */
    fun setDateIdentifier(identifier: String): JsonProvider

    //var extensionClasses: Map<String, Class<*>>
    //var dateFormatPattern: SimpleDateFormat
    //var dateIdentifier: String
    //    fun <T> fromJson(json: String, klass: Class<T>) : T
//    fun <T> fromJson(json: JsonValue, klass: Class<T>) : T

    /**
     * @param string the string to be serialized
     * @return The [JsonString] instance
     */
    fun newValue(string: String): JsonString

    /**
     * @param dateString the string to be parsed into a Date object using the provider's date pattern.
     *
     * Use provider.setDateFormat() to set the date pattern.
     *
     * @return The [JsonDate] instance or null if the string does not conforms with the provider's date pattern.
     *
     * In such a case, an exception message will be print on the console
     */
    fun newDateValue(dateString: String): JsonDate?

    /**
     * @param dateString the string to be parsed into a Date object
     * @return The [JsonDate] instance or null if the string does not conforms with the provider's date pattern.
     *
     * In such a case, an exception message will be print on the console
     */
    fun newValue(value: Date): JsonDate

    /**
     * @param int the int to be encapsulated into a JsonInt instance.
     * @return The [JsonInt] instance
     *
     */
    fun newValue(int: Int): JsonInt

    /**
     * @param boolean the boolean to be encapsulated into a JsonBoolean instance.
     * @return The [JsonBoolean] instance
     *
     */
    fun newValue(boolean: Boolean): JsonBoolean

    /**
     * @param float the float to be encapsulated into a [JsonFloat] instance.
     * @return The [JsonFloat] instance
     *
     */
    fun newValue(float: Float): JsonFloat

    /**
     * @param double the int to be encapsulated into a JsonDouble instance.
     * @return The [JsonDouble] instance
     *
     */
    fun newValue(double: Double): JsonDouble

    /**
     * @param any User this method to create a generic JsonValue. Useful to encapsulate your custom classes.
     * @return The [JsonValue] instance. It can be one of the primitive types([JsonInt], [JsonString] ecc) or one of [JsonObject] or [JsonArray]
     *
     */
    fun newValue(any: Any): JsonValue

    /**
     * @param exception the exception to be encapsulated into a JsonValue instance.
     * Only the exception class and the exception message will be used.
     * @return The [JsonValue] instance. It can be [JsonObject] or [JsonArray].
     */
    fun newValue(exception: Throwable): JsonValue
//Adds the stacktrace array of an [Exception] as an extension member.
    /**
     *  =======> ****SECURITY ALERT**** <=========
     *
     *  Although it is not advisable to add the stacktrace of an exception as a member extensions,
     *  it can be helpful when debugging a system.
     *  It should be avoided in production since exception messages and stacktrace contains information on the implementation's internals
     *  and therefore can expose your system to **security threats**.
     *
     *  **Use at your own risk.**
     *
     *  <========================================>
     *
     *
     * Creates a new JsonArray with each StackTrace object serialized as a JsonObject.
     * You should do not use this method directly, instead you can use the corresponding
     * method on the ProblemBuilder class: [ProblemBuilder.addExtension].
     *
     *
     *``` kotlin
     *  val problemClassic = Problem.create(provider)
     *    ... // other builder calls ...
     *    .addExtension(exception.stackTrace, depth = 3, excludePackages = arrayOf("*junit*", "java.lang.*"))
     *    .build()
     * ```
     * The resulting JSON String will be:
     *```
     * {
     *  some json element...,
     *  some other json element...,
     *  "stacktrace":[{
     *      "classLoaderName":"app",
     *      "declaringClass":"org.javando.http.problem.impl.test.GsonProviderTest",
     *      "methodName":"integrateAll",
     *      "fileName":"GsonProviderTest.kt",
     *      "lineNumber":152,
     *      "format":1
     *      }, {
     *      "moduleName":"java.base",
     *      "moduleVersion":"11.0.4",
     *      "declaringClass":"java.util.ArrayList",
     *      "methodName":"forEach",
     *      "fileName":"ArrayList.java",
     *      "lineNumber":1540,
     *      "format":2
     *      },
     *      {
     *      "moduleName":"java.base",
     *      "moduleVersion":"11.0.4",
     *      "declaringClass":"java.util.ArrayList",
     *      "methodName":"forEach",
     *      "fileName":"ArrayList.java",
     *      "lineNumber":1540,
     *      "format":2
     *      }]
     *  }
     *```
     *
     * In this example we have added the stacktrace array of an exception, limiting it's `depth` to 3 elements at most,
     * and by excluding the packages starting with **`java.lang.`** or that contains the substring **`junit`**
     *
     * **Note the use of the `*` : It means any character, as usual.**
     *
     * **Note also that regular expressions can't be used, you can use only the `*`**
     *
     * @param stacktraceArray the stacktrace array to be encapsulated into a JsonArray.
     *
     * @return The [JsonArray] instance containing, for each StackTrace object, a corresponding JSON object.
     */
    fun newValue(stacktraceArray: Array<StackTraceElement>, properties: Properties): JsonArray

    companion object Defaults {
        const val defaultDatePattern = "dd/MM/yyyy hh:mm:ss"
        const val defaultDateIdentifier = "date"
        private val camelCaseToSnakeCasePattern = Pattern.compile("(^.)|([a-z])([A-Z])")

        fun toSnakeCase(camelCaseClass: String): String {
            return camelCaseToSnakeCasePattern.matcher(camelCaseClass)
                .replaceAll("""${'$'}1${'$'}2_${'$'}3""")
                .replaceFirst("_", "")
                .toLowerCase()
        }

        fun <T : Any> toSnakeCase(klass: KClass<T>): String {
            return toSnakeCase(klass::class.java.simpleName)
        }

        fun <T> toSnakeCase(klass: Class<T>): String {
            return toSnakeCase(klass.simpleName)
        }
    }
}


