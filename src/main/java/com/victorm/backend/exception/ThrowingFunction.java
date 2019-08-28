package com.victorm.backend.exception;

@FunctionalInterface
public interface ThrowingFunction<P, R, E extends Exception> {

    R apply(P param) throws E;
}
