package ru.justfrogdev.config;

/**
 * Утилитный класс для работы с переменными окружения.
 * Поддерживает как системные переменные (System.getenv),
 * так и значения из application.yml с подставновкой переменных через ${...}.
 *
 * Использование:
 * - Установить env-переменные: TEST_API_URL=..., TEST_DB_PASSWORD=..., и т.д.
 * - Или скопировать .env.example в .env и запустить с dotenv или IDE интеграцией
 * - Или использовать дефолтные значения из application.yml
 */
public final class EnvironmentVariableConfig {

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

    private EnvironmentVariableConfig() {
        // Утилитный класс, не должен быть инстанцирован
    }
}
