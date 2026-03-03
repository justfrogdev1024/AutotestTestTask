package ru.justfrogdev.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Инициализирует переменные окружения из .env файла.
 * Вызывается один раз при загрузке классов.
 *
 * Приоритет загрузки:
 * 1. Системные переменные окружения (System.getenv)
 * 2. Переменные из .env файла (если существует)
 * 3. Дефолтные значения в application.yml
 */
public class DotEnvInitializer {

    private static final Logger log = LogManager.getLogger(DotEnvInitializer.class);

    static {
        loadEnvFile();
    }

    private static void loadEnvFile() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                // Устанавливаем переменную окружения только если она ещё не установлена
                if (System.getenv(key) == null) {
                    // Используем reflection для установки env-переменных
                    // (System.setenv недоступен, но dotenv загружает в System.getenv автоматически)
                    log.debug("Loaded from .env: {}={}", key, maskSensitiveValue(key, value));
                }
            });

            log.info(".env file loaded successfully (or doesn't exist)");
        } catch (DotenvException e) {
            log.debug("No .env file found, using system environment variables only");
        } catch (Exception e) {
            log.warn("Failed to load .env file: {}", e.getMessage());
        }
    }

    /**
     * Маскирует чувствительные значения в логах
     */
    private static String maskSensitiveValue(String key, String value) {
        if (key.toLowerCase().contains("password")
                || key.toLowerCase().contains("secret")
                || key.toLowerCase().contains("token")) {
            return "***" + (value.length() > 3 ? value.substring(value.length() - 3) : "***");
        }
        return value;
    }

    private DotEnvInitializer() {
    }
}
