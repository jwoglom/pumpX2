package com.jwoglom.pumpx2.cliparser.util;

import java.util.function.Function;

/**
 * The environment variables cliparser commands read. The process environment for a one-shot
 * command; the current request's variables while Main's serve mode runs one.
 */
public final class Env {
    private static Function<String, String> source = System::getenv;

    private Env() {}

    public static String get(String name) {
        return source.apply(name);
    }

    public static void use(Function<String, String> newSource) {
        source = newSource;
    }

    public static void useProcessEnvironment() {
        source = System::getenv;
    }
}
