package javax.rfc7807.api;

import javax.rfc7807.impl.GsonProvider;

public class Problems {

    private static final GsonProvider defaultProvider = new GsonProvider();

    public static Problem from(String json) {
        return Problem.Companion.from(json , defaultProvider);
    }

}
