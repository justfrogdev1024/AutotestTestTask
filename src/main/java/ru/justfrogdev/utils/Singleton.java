package ru.justfrogdev.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ru.justfrogdev.utils.models.EnvironmentConfig;
import ru.justfrogdev.config.DotEnvInitializer;

import java.io.IOException;
import java.util.Map;

public final class Singleton {

    private static ObjectMapper jsonObjectMapper;
    private static ObjectMapper yamlObjectMapper;
    private static EnvironmentConfig environmentConfig;

    private Singleton() {
    }

    // Инициализируем .env при загрузке класса
    static {
        // Ссылка на класс гарантирует его загрузку
        try {
            Class.forName(DotEnvInitializer.class.getName());
        } catch (ClassNotFoundException e) {
            // Не должно быть, но на всякий случай обработаем
            throw new RuntimeException("Failed to initialize DotEnvInitializer", e);
        }
    }

    public static synchronized ObjectMapper getJsonObjectMapper() {
        if (jsonObjectMapper == null) {
            jsonObjectMapper = new ObjectMapper().findAndRegisterModules();
        }
        return jsonObjectMapper;
    }

    public static synchronized ObjectMapper getYamlObjectMapper() {
        if (yamlObjectMapper == null) {
            yamlObjectMapper = new ObjectMapper(new YAMLFactory()).findAndRegisterModules();
        }
        return yamlObjectMapper;
    }

    public static synchronized EnvironmentConfig getEnvironmentConfig() {
        if (environmentConfig == null) {
            Map<String, EnvironmentConfig> environmentConfigByName;
            try {
                environmentConfigByName = Singleton.getYamlObjectMapper().readValue(
                        ClassLoader.getSystemResource("application.yml"),
                        new TypeReference<>() {
                        }
                );
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read 'application.yml'", e);
            }

            String environmentName = System.getProperty("environment", "test");
            if (!environmentConfigByName.containsKey(environmentName)) {
                String message = String.format(
                        "Failed to read '%s' configuration, please application.yml",
                        environmentName);
                throw new IllegalStateException(message);
            }

            environmentConfig = environmentConfigByName.get(environmentName);
        }
        return environmentConfig;
    }
}
