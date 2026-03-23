package ru.justfrogdev.config;

public final class EnvironmentVariableConfig {

    public static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    private EnvironmentVariableConfig() {
        // Утилитный класс, не должен быть инстанцирован
    }
}
