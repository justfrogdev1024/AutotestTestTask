# AutotestTestTask

Автоматизированные тесты для API, UI и мобильных приложений на Java + Selenium + TestNG.

## 🚀 Quick Start

### Шаг 1: Установка зависимостей

```bash
mvn clean install
```

### Шаг 2: Настройка переменных окружения

Создайте файл `.env` на основе `.env.example`:

```bash
cp .env.example .env
```

Отредактируйте `.env` и добавьте реальные учётные данные:

```env
TEST_API_URL=https://your-api.com/v2
TEST_API_USERNAME=your_username
TEST_API_PASSWORD=your_password

TEST_UI_URL=https://your-app.com
TEST_UI_USERNAME=ui_user
TEST_UI_PASSWORD=ui_pass

TEST_DB_URL=jdbc:postgresql://your-db:5432/database
TEST_DB_USERNAME=db_user
TEST_DB_PASSWORD=db_pass
```

**⚠️ Важно:** Файл `.env` в `.gitignore` и никогда не коммитится в репозиторий.

### Шаг 3: Запуск тестов

```bash
# Все тесты
mvn test

# Только UI-тесты
mvn test -DsuiteXmlFile=src/test/resources/testng-ui.xml

# Только API-тесты
mvn test -DsuiteXmlFile=src/test/resources/testng-api.xml

# С конкретной окружением
mvn test -Denvironment=production

# С отключением логирования
mvn test -DenableLogging=false
```

### Шаг 4: Просмотр отчётов

```bash
# Сгенерировать HTML-отчет Allure
mvn allure:report

# Открыть в браузере
allure open target/allure-report
```

## 📋 Структура проекта

```
src/
├── main/
│   ├── java/ru/justfrogdev/
│   │   ├── config/              # Spring конфигурация, загрузка env
│   │   ├── listeners/           # TestNG/Allure слушатели
│   │   ├── ui/
│   │   │   └── pages/          # Page Object паттерн
│   │   ├── utils/
│   │   │   ├── models/         # конфиги (API, UI, DB)
│   │   │   ├── CLF.java        # логирование REST-запросов
│   │   │   └── RestClients.java # REST-клиент с логированием
│   │   └── api/                # API clients (если добавится)
│   └── resources/
│       └── application.yml     # конфигурация с переменными окружения
├── test/
│   ├── java/
│   │   ├── ui/tests/          # UI тесты
│   │   ├── api/tests/         # API тесты
│   │   └── mobile/tests/      # мобильные тесты
│   └── resources/
│       └── testng*.xml        # конфиги TestNG
├── .env.example               # шаблон переменных окружения
└── pom.xml                    # Maven конфигурация
```

## 🔐 Безопасность учётных данных

### Как это работает:

1. **application.yml** содержит плейсхолдеры с env-переменными:
   ```yaml
   test:
     api:
       url: ${TEST_API_URL:https://petstore.swagger.io/v2}
       password: ${TEST_API_PASSWORD:admin}
   ```

2. **DotEnvInitializer** загружает значения из `.env` при старте:
   - Первый приоритет: системные переменные окружения
   - Второй приоритет: переменные из `.env`
   - Третий приоритет: дефолтные значения в YAML

3. **Логирование** автоматически маскирует пароли:
   ```
   Loaded from .env: TEST_API_PASSWORD=***123
   ```

### Установка env-переменных (альтернативы):

**Вариант 1: IDE (IntelliJ IDEA)**
- Edit Configurations → Run → Environment variables
- Добавить: `TEST_API_PASSWORD=admin`

**Вариант 2: Maven**
```bash
mvn test -DTEST_API_PASSWORD=admin -DTEST_DB_PASSWORD=secret
```

**Вариант 3: Linux/Mac shell**
```bash
export TEST_API_PASSWORD=admin
mvn test
```

**Вариант 4: Windows CMD**
```cmd
set TEST_API_PASSWORD=admin
mvn test
```

## 🧪 Примеры тестов

### API-тест с логированием

```java
@Test
public void testGetPetById() {
    RestClients.api()
        .get("/pet/1")
        .then()
        .statusCode(200)
        .body("name", equalTo("Fluffy"));
}
```

Логирует автоматически:
```
Request method: GET
Request URI: https://petstore.swagger.io/v2/pet/1
Response Status Code: 200
Response Body: {...}
```

### UI-тест с явными ожиданиями

```java
@Test
public void testLoginFlow() {
    ExampleMainPage page = new ExampleMainPage(driver, config.url());
    page.get()
        .fillUsername("user@example.com")
        .fillPassword("password")
        .clickLogin()
        .waitForDashboard();
}
```

## ⚙️ Конфигурация

### Параллельное выполнение (pom.xml)

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>classes</parallel>
        <threadCount>3</threadCount>
        <forkCount>2</forkCount>
    </configuration>
</plugin>
```

### Retry механизм

Не-стабильные тесты автоматически переходят 2 раза через `RetryAnalyzer`.

### Логирование

- **Включено по дефолту**, отключить: `-DenableLogging=false`
- Логи идут в **Log4j2** (см. `log4j2.xml` при необходимости)

## 📝 Лучшие практики

1. **Используй env-переменные**, не хардкодируй credentials
2. **Маскируй пароли в логах** (делается автоматически в CLF и DotEnvInitializer)
3. **Page Object паттерн** для UI-тестов
4. **Явные ожидания** вместо `ImplicitWait`
5. **Retry для нестабильных тестов** только если необходимо
6. **Группируй тесты** по функциональности (UI, API, DB)

## 🐛 Известные ограничения

- Пароли в логах маскируются по ключевому слову "password" в имени переменной
- `.env` файл не коммитится (добавлен в `.gitignore`)

## 📞 Контакты

Автор: @Archer1024

---

**Обновлено:** Март 2026
