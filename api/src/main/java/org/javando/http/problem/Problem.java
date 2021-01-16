package org.javando.http.problem;

import java.util.Objects;

import static java.lang.String.format;

public abstract class Problem implements ProblemKt {

    protected final transient JsonProvider jsonProvider;

    protected Problem(JsonProvider provider) {
        jsonProvider = provider;
    }

    public static Problem from(String json, JsonProvider provider) {
        return provider.fromJson(json);
    }

    public static ProblemBuilderClassic create(JsonProvider provider) {
        return new ProblemReferenceImplementation.BuilderClassic(provider);
    }

    public static ProblemBuilderWither wither(JsonProvider provider) {
        return new ProblemReferenceImplementation.Builder(provider);
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