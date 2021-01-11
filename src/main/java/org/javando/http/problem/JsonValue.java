package org.javando.http.problem;

import java.util.Date;
import java.util.Optional;

//import static org.javando.http.problem.ReflectKt.getDefaultProviderOrThrow;

/**
 * This is the base class for all JsonValue types. <br>
 * You can easily obtain instances of many basic types with the static helper methods provided, for example:
 * <br><br>
 * <code>
 *     var myValue = JsonValue.of("My json string");
 * </code>
 * <br><br>
 * Usually you do not directly use this class or its helper methods, since the {@link Problem} class
 * has its own helper methods to fully build a <code>Problem</code> instance.
 **/
public interface JsonValue extends JsonValueKt {

    final class Internal {
        private static JsonProvider jsonProvider;

        private Internal() { }

        public static JsonProvider getJsonProvider() {
            return Optional.ofNullable(jsonProvider).orElseThrow(() ->
                    new JsonProviderNotSetException("Cannot invoke static method JsonValue.of(...) without setting the JsonProvider with setJsonProvider"));
        }
    }

    static void setJsonProvider(JsonProvider provider) {
        Internal.jsonProvider = provider;
    }

    /**
     * Creates a new JsonString instance based on the provider's implementation
     * @param string The string to be encapsulated in a JsonString instance
     * @return The JsonString instance
     */
    static JsonString of(String string) {
        return Internal.getJsonProvider().newValue(string);
    }

    /**
     * Creates a new JsonDate instance based on the provider's implementation
     * @param date The date object
     * @return The corresponding JsonDate instance
     */

    static JsonDate of(Date date) {
        return Internal.getJsonProvider().newDateValue(date);
    }

    /**
     * Creates a new JsonDate instance using the provider's date format. To customize the date format see {@link JsonProvider}
     * @param string The string to be parsed
     * @return The JsonDate instance
     * @throws JsonDate.InvalidDateStringException If the string cannot be converted to a JsonDate object
     */

    static JsonDate ofDate(String string) {
        return Internal.getJsonProvider().newDateValue(string);
    }

    /**
     * Creates a new JsonInt instance based on the provider's implementation
     * @param mInt The integer to be encapsulated in a JsonInt instance
     * @return The JsonInt instance
     */
    static JsonInt of(int mInt) {
        return Internal.getJsonProvider().newValue(mInt);
    }

    /**
     * Creates a new JsonFloat instance based on the provider's implementation
     * @param mFloat The string to be encapsulated in a JsonFloat instance
     * @return The JsonFloat instance
     */
    static JsonFloat of(float mFloat) {
        return Internal.getJsonProvider().newValue(mFloat);
    }

    /**
     * Creates a new JsonDouble instance based on the provider's implementation
     * @param mDouble The string to be encapsulated in a JsonDouble instance
     * @return The JsonDouble instance
     */
    static JsonDouble of(double mDouble) {
        return Internal.getJsonProvider().newValue(mDouble);
    }

    /**
     * Creates a new JsonBoolean instance based on the provider's implementation
     * @param mBoolean The boolean to be encapsulated in a JsonBoolean instance
     * @return The JsonBoolean instance
     */
    static JsonBoolean of(boolean mBoolean) {
        return Internal.getJsonProvider().newValue(mBoolean);
    }

    /**
     * Creates a new JsonValue instance based on the provider's implementation. It can be one of
     * the primitive types or one of {@link JsonObject} or {@link JsonArray}
     * @param mObject The string to be encapsulated in a JsonValue instance
     * @return The JsonValue instance
     */
    static JsonValue of(Object mObject) {
        return Internal.getJsonProvider().newValue(mObject);
    }
}

