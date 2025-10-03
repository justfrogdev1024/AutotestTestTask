package ru.justfrogdev.utils.models;

public record EnvironmentConfig(
        ApiConfig api,
        DatabaseConfig db,
        UiConfig ui
) {
}
