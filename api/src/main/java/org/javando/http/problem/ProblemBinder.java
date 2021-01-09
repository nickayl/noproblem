package org.javando.http.problem;

public interface ProblemBinder {
    Class<? extends Problem.Builder> getImplementation(JsonProvider provider);
}
