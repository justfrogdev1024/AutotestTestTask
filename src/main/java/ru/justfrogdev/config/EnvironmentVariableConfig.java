package ru.justfrogdev.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Загружает переменные окружения в Spring конфигурацию.
 * Поддерживает как системные переменные (System.getenv),
 * так и значения из application.yml с подставновкой переменных.
 *
 * Использование:
 * - Установить env-переменные: TEST_API_URL=..., TEST_DB_PASSWORD=..., и т.д.
 * - Или скопировать .env.example в .env и запустить с dotenv или IDE интеграцией
 */
@Configuration
public class EnvironmentVariableConfig {

    /**
     * Получить значение переменной окружения с резервным значением по умолчанию
     * @param envVar имя переменной окружения
     * @param defaultValue резервное значение
     * @return значение переменной или резервное значение
     */
    public static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }
}
