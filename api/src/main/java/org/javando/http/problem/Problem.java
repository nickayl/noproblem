package org.javando.http.problem;

public abstract class Problem implements ProblemKt {

    protected final JsonProvider jsonProvider;

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

}