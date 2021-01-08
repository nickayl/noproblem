package javax.rfc7807.api;

import javax.rfc7807.impl.GsonProvider;
import java.util.Optional;

public class Providers {

    private static final JsonProvider DEFAULT_PROVIDER = new GsonProvider();
    private static JsonProvider selectedProvider;

    static {
        try {
            selectedProvider = (JsonProvider) Class
                    .forName("javax.rfc7807.provider.ProviderImpl")
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            selectedProvider = DEFAULT_PROVIDER;
            e.printStackTrace();
        }
    }

    public static JsonProvider getDefault() {
        return DEFAULT_PROVIDER;
    }

    public static JsonProvider getSelected() {
        return Optional.ofNullable(selectedProvider).orElse(DEFAULT_PROVIDER);
    }

}
