package javax.rfc7807.api;

import javax.rfc7807.impl.GsonProvider;

public class Providers {

    private static JsonProvider DEFAULT_PROVIDER;
    private static JsonProvider selectedProvider;

    static {
        try {
            selectedProvider = (JsonProvider) Class
                    .forName("javax.rfc7807.provider.ProviderImpl")
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            selectedProvider = DEFAULT_PROVIDER = new GsonProvider();
            e.printStackTrace();
        }
    }

    public static JsonProvider getDefault() {
        return DEFAULT_PROVIDER;
    }

    public static JsonProvider getSelected() {
        return selectedProvider;
        //return Optional.ofNullable(selectedProvider).orElse(DEFAULT_PROVIDER);
    }

}
