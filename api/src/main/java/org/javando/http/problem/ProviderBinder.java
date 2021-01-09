package org.javando.http.problem;

public interface ProviderBinder {
    Class<? extends JsonProvider> getImplementation();
}
