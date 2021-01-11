package org.javando.http.problem;

import static org.javando.http.problem.ReflectKt.findProblemBuilderOrThrow;
import static org.javando.http.problem.ReflectKt.getDefaultProviderOrThrow;

public abstract class Problem implements ProblemKt {

    public static Problem from(String json) {
        return from(json, getDefaultProviderOrThrow());
    }
    public static Problem from(String json, JsonProvider provider) {
        return provider.fromJson(json);
    }

    public static ProblemBuilder create() { return create(getDefaultProviderOrThrow()); }
    public static ProblemBuilder create(JsonProvider provider) {
        return findProblemBuilderOrThrow(provider);
    }

}