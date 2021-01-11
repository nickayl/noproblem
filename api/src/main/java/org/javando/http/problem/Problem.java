package org.javando.http.problem;

public abstract class Problem implements ProblemKt {

    protected final JsonProvider jsonProvider;

    protected Problem(JsonProvider provider) {
        jsonProvider = provider;
    }

    public static Problem from(String json, JsonProvider provider) {
        return provider.fromJson(json);
    }

    public static ProblemBuilder create(JsonProvider provider) {
        return new ProblemReferenceImplementation.Builder(provider);
    }

}