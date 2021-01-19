package org.javando.http.problem;

import java.util.Objects;

import static java.lang.String.format;

public abstract class Problem implements ProblemKt {

    protected final transient JsonProvider jsonProvider;
    public static final transient String mediaType = "application/problem+json";

    protected Problem(JsonProvider provider) {
        jsonProvider = provider;
    }

    /**
     * Construct a new Problem instance from the given string using the specified {@link JsonProvider}
     *
     * @param json The JSON string representing a JSON Object compliant with the <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a> specification.
     * @return The deserialized {@link Problem} instance.
     * @throws InvalidJsonStringException if the string is not a valid JSON object
     * or if the string is not compliant with the <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a>.
     */
    public static Problem from(String json, JsonProvider provider) {
        return provider.fromJson(json);
    }

    /**
     * @param provider The provider used as JSON engine. (GsonProvider. for example)
     * @return a new {@link ProblemBuilderClassic} instance in the 'classic' form without prefixed 'with' in the method names.
     */
    public static ProblemBuilderClassic create(JsonProvider provider) {
        return new ProblemReferenceImplementation.BuilderClassic(provider);
    }

    /**
     * @param provider The provider used as JSON engine. (GsonProvider. for example)
     * @return a new {@link ProblemBuilderWither} instance in the 'wither' form with the prefixed 'with' in the method names.
     */
    public static ProblemBuilderWither wither(JsonProvider provider) {
        return new ProblemReferenceImplementation.Builder(provider);
    }

    /**
     * The media type assigned to the RFC 7807. <br>
     * It can be used, for example, in an HTTP response header.
     */
    public static String getMediaType() {
        return mediaType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Problem)) return false;
        Problem problem = (Problem) o;
        return Objects.equals(getTitle(), problem.getTitle()) &&
                Objects.equals(getType(), problem.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getType());
    }

    @Override
    public String toString() {
        return format("%s(%s;%s;%s;%s;%d)",getClass().getSimpleName(), getTitle(), getDetails(), getType(), getInstance(), getStatus().value());
    }
}