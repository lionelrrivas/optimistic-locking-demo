package com.lionelrivas.service;

@SuppressWarnings("unchecked")
public interface ThrowingConsumer {
    static <T extends Exception> void consumeOrThrow(Exception e) throws T {
        throw (T) e;
    }
}
