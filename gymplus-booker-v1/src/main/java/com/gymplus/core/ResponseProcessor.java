package com.gymplus.core;

@FunctionalInterface
public interface ResponseProcessor {
    Gmap process(String body, int status);
}
