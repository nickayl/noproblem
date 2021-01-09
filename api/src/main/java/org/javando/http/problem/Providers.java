package org.javando.http.problem;

public class Providers {

    //private static JsonProvider DEFAULT_PROVIDER;
    private static final JsonProvider selectedProvider;

    static {
        try {
            var binder = (ProviderBinder) Class
                    .forName("org.javando.http.problem.impl.ProviderBinderImpl")
                    .getDeclaredConstructor()
                    .newInstance();
            selectedProvider = binder.getImplementation()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            //selectedProvider = DEFAULT_PROVIDER = new GsonProvider();
            e.printStackTrace();
            throw new MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e);
        }
    }

//    public static JsonProvider getDefault() {
//        return DEFAULT_PROVIDER;
//    }

    public static JsonProvider getSelected() {
        return selectedProvider;
        //return Optional.ofNullable(selectedProvider).orElse(DEFAULT_PROVIDER);
    }

}
