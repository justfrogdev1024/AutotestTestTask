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
mvn clean test

# Фильтрация по группам TestNG
mvn clean test -DrequiredGroups=ui           # Только UI-тесты
mvn clean test -DrequiredGroups=mobile       # Только мобильные тесты
mvn clean test -DrequiredGroups=mobile,ui    # Тесты с обеими группами

# Выбор окружения
mvn clean test -Denvironment=production -DrequiredGroups=smoke

# Отключение логирования
mvn clean test -DenableLogging=false

# Тесты + Allure отчёт одной командой
mvn clean test -DrequiredGroups=ui allure:report
```

### Шаг 4: Allure отчёты

```bash
# Генерация HTML отчёта
mvn allure:report

# Запуск локального веб-сервера с отчётом (автоматически откроется в браузере)
mvn allure:serve

# Очистка старых результатов
mvn allure:clean
```

**Расположение файлов:**
- Результаты тестов: `target/allure-results/`
- HTML отчёт: `target/allure-report/`

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

## 🗄️ SQL Логирование

Проект использует **P6Spy** для автоматического логирования всех SQL запросов к базе данных.

### Как это работает

1. JDBC URL использует префикс `jdbc:p6spy:postgresql://...` вместо `jdbc:postgresql://...`
2. P6Spy перехватывает все запросы и логирует их через SLF4J → Log4j2
3. В логах видны:
   - SQL запрос с подставленными параметрами (вместо `?`)
   - Время выполнения в миллисекундах
   - Timestamp выполнения

### Пример лога

```
2026-03-03 14:25:31.123 | SQL: SELECT * FROM users WHERE id = 42 AND status = 'active' | Время выполнения: 15 ms
2026-03-03 14:25:31.145 | SQL: INSERT INTO orders (user_id, amount) VALUES (42, 199.99) | Время выполнения: 8 ms
```

### Настройка

**Включить SQL логирование:**
```env
TEST_DB_URL=jdbc:p6spy:postgresql://localhost:5432/mydb
```

**Отключить SQL логирование:**
```env
TEST_DB_URL=jdbc:postgresql://localhost:5432/mydb
```

**Конфигурация:** `src/main/resources/spy.properties`
- Формат логов
- Порог времени выполнения (executionThreshold)
- Включение/отключение стек-трейсов

### Фильтрация медленных запросов

Изменить `executionThreshold` в `spy.properties`:

```properties
# Логировать только запросы дольше 100 мс
executionThreshold=100
```

## ⏱️ WebDriver Wait Strategy

Проект использует **только Explicit Waits** для максимальной стабильности и скорости тестов.

### Почему не Implicit Wait?

❌ **Не используется:**
```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // УДАЛЕНО
```

**Проблемы Implicit Wait:**
- Конфликтует с Explicit Wait (непредсказуемое время ожидания)
- Замедляет тесты (ждёт даже когда не нужно)
- Проверяет только наличие в DOM (не visibility/clickability)

### UIWaitHelper

Все ожидания централизованы в `UIWaitHelper`:

```java
UIWaitHelper wait = new UIWaitHelper(driver);

// Ждать пока элемент станет видимым
wait.waitForVisible(By.id("button"));

// Ждать пока элемент станет кликабельным
wait.waitForClickable(By.id("button"));

// Ждать появления текста
wait.waitForTextToBe(By.id("status"), "Success");

// Ждать исчезновения элемента
wait.waitForInvisible(By.id("loader"));
```

### Использование в Page Object

```java
public class LoginPage {
    private final WebDriver driver;
    private final UIWaitHelper wait;
    
    private final By usernameField = By.id("username");
    private final By loginButton = By.id("login-btn");
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new UIWaitHelper(driver);
    }
    
    public void login(String username) {
        // Явно ждём, пока поле станет видимым
        wait.waitForVisible(usernameField).sendKeys(username);
        
        // Явно ждём, пока кнопка станет кликабельной
        wait.waitForClickable(loginButton).click();
    }
}
```

### AbstractPage

Для наследников `AbstractPage` доступны готовые методы:

```java
public class MyPage extends AbstractPage {
    public void clickSubmit() {
        elementAwait(By.id("submit")); // waitForVisible + waitForClickable
        click(By.id("submit"));
    }
}
```

**Доступные методы:**
- `elementAwait(By)` — ждать visibility + clickability
- `elementVisibilityAwait(By)` — только visibility
- `noElementAwait(By)` — ждать исчезновения

### Best Practices

✅ **Правильно:**
```java
wait.waitForClickable(loginButton).click();
```

❌ **Неправильно:**
```java
driver.findElement(loginButton).click(); // Может упасть, если элемент ещё не загружен
```

## 🏷️ TestNG Группы и фильтрация

Тесты организованы по группам для удобной фильтрации:

### Доступные группы

- **ui** — UI-тесты (Selenium WebDriver)
- **mobile** — мобильные тесты (Appium)
- **api** — API-тесты (REST Assured) _(если добавите)_
- **smoke** — быстрые smoke-тесты _(если добавите)_
- **regression** — полная регрессия _(если добавите)_

### Примеры команд

```bash
# Запустить все UI-тесты
mvn clean test -DrequiredGroups=ui

# Запустить только мобильные тесты
mvn clean test -DrequiredGroups=mobile

# Запустить тесты с несколькими группами (AND)
mvn clean test -DrequiredGroups=mobile,ui

# Smoke-тесты для быстрой проверки
mvn clean test -DrequiredGroups=smoke
```

### Как добавить группу к тесту

```java
@Test(groups = {"ui", "smoke"})
public void testLoginFlow() {
    // Быстрый тест логина
}

@Test(groups = {"ui", "regression"})
public void testComplexScenario() {
    // Полный сценарий
}
```

### GroupIntersectionInterceptor

Проект использует кастомный интерсептор, который фильтрует тесты по **пересечению** групп:

- `-DrequiredGroups=ui` → запустит тесты, содержащие группу "ui"
- `-DrequiredGroups=mobile,ui` → запустит тесты, содержащие **И** "mobile", **И** "ui"

## ⚡ Параллелизм тестов

### Текущие настройки

```xml
<parallel>classes</parallel>       <!-- Параллелизм на уровне классов -->
<threadCount>3</threadCount>       <!-- 3 потока -->
<forkCount>2</forkCount>           <!-- 2 JVM процесса -->
```

**Что это значит:**
- Каждый тестовый класс запускается в отдельном потоке
- Методы внутри класса выполняются последовательно
- Максимум 3 класса одновременно

### Изоляция данных

✅ **WebDriver изолирован** — каждый тест создаёт свой экземпляр в `@BeforeMethod`

⚠️ **БД/API данные** — используйте уникальные значения:
```java
String email = "user-" + UUID.randomUUID() + "@test.com";
User user = createUser(email, "password123");
```

### Отключение параллелизма

Если тесты нестабильны при параллельном запуске:

```bash
mvn clean test -DthreadCount=1
```

Или закомментировать `<parallel>` в `pom.xml`.

## 🐛 Известные ограничения

- Пароли в логах маскируются по ключевому слову "password" в имени переменной
- `.env` файл не коммитится (добавлен в `.gitignore`)
- SQL логирование увеличивает объём логов (можно отключить через JDBC URL)

## 📞 Контакты

Автор: @Archer1024

---

**Обновлено:** Март 2026
