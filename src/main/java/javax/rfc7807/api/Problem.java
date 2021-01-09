package javax.rfc7807.api;

import javax.rfc7807.impl.ProblemReferenceImplementation;

public abstract class Problem implements ProblemKt {

    public static Problem.Builder create() {
        return create(Providers.getSelected());
    }

    public static Problem.Builder create(JsonProvider provider) {
        return new ProblemReferenceImplementation.Builder(provider);
    }

    public static Problem from(String json) {
        return from(json, Providers.getSelected());
    }

    public static Problem from(String json, JsonProvider provider) {
        return provider.fromJson(json);
    }

}
