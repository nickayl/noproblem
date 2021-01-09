package org.javando.http.problem;

public interface Problem extends ProblemKt {

    static Problem.Builder create() {
        return create(Providers.getSelected());
    }

    static Problem.Builder create(JsonProvider provider) {
        try {
            var binder = (ProblemBinder) Class.forName("org.javando.http.problem.impl.ProblemBinderImpl")
                    .getDeclaredConstructor()
                    .newInstance();
            return binder.getImplementation(provider)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e);
        }
        //return new ProblemReferenceImplementation.Builder(provider);
    }

    static Problem from(String json) {
        return from(json, Providers.getSelected());
    }

    static Problem from(String json, JsonProvider provider) {
        return provider.fromJson(json);
    }

}
