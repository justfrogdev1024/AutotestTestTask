package ru.justfrogdev.utils.models;

public record DatabaseConfig(
        String url,
        String username,
        String password
) {
}
