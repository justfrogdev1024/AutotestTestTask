package ru.justfrogdev.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
                    // dotenv загружает значения в System.getenv автоматически
                    log.debug("Загружено из .env: {}={}", key, maskSensitiveValue(key, value));
                }
            });

            log.info("Файл .env загружен успешно (или не существует)");
        } catch (DotenvException e) {
            log.debug("Файл .env не найден, используются системные переменные окружения");
        } catch (Exception e) {
            log.warn("Ошибка при загрузке .env файла: {}", e.getMessage());
        }
    }
    
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
