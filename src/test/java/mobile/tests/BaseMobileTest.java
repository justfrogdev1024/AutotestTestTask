package mobile.tests;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.connection.ConnectionStateBuilder;
import io.appium.java_client.android.connection.HasNetworkConnection;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import ru.justfrogdev.utils.CLF;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Базовый класс для мобильных Android‑тестов на Appium.
 * Подклассы переопределяют {@link #getAppPackage()} и {@link #getAppActivity()}.
 * Таймаут создания сессии: {@value #SESSION_CREATION_TIMEOUT_SEC} сек (зависит от запуска приложения на устройстве).
 */
public abstract class BaseMobileTest {

    private static final int SESSION_CREATION_TIMEOUT_SEC = 120;

    protected AndroidDriver driver;

    /** Пакет приложения (например, com.vk.vkvideo). */
    protected abstract String getAppPackage();

    /** Главная activity. Если null/пусто — не задаём (для части приложений сессия создаётся быстрее без указания activity). */
    protected String getAppActivity() {
        return null;
    }

    /**
     * Если true, приложение не запускается при создании сессии (только appPackage в capabilities).
     * После создания сессии приложение открывается через activateApp(package). Помогает избежать зависания на долгом splash/логине.
     */
    protected boolean createSessionWithoutLaunchingApp() {
        return false;
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpAndroidDriver() throws MalformedURLException {
        System.out.println("[BaseMobileTest] setUpAndroidDriver: начало");

        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName("Android");
        options.setPlatformVersion("11.0");
        options.setDeviceName("Android Emulator");
        options.setAutomationName("UiAutomator2");
        options.setNewCommandTimeout(Duration.ofSeconds(600));

        String appPackage = getAppPackage();
        String activity = getAppActivity();
        boolean skipLaunch = createSessionWithoutLaunchingApp();

        if (!skipLaunch) {
            options.setAppPackage(appPackage);
            if (activity != null && !activity.isEmpty()) {
                options.setAppActivity(activity);
            }
        }
        options.setNoReset(true);

        URL appiumServer = new URL("http://127.0.0.1:4723");
        System.out.println("[BaseMobileTest] Подключение к Appium: " + appPackage + (skipLaunch ? " (сессия без запуска приложения)" : " / " + activity) + ", таймаут " + SESSION_CREATION_TIMEOUT_SEC + " сек");

        Callable<AndroidDriver> createDriver = () -> new AndroidDriver(appiumServer, options);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            driver = executor.submit(createDriver).get(SESSION_CREATION_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(
                    "Создание сессии Appium не завершилось за " + SESSION_CREATION_TIMEOUT_SEC + " сек. "
                    + "Проверьте: 1) В окне Appium — доходит ли запрос, нет ли ошибок. "
                    + "2) Эмулятор запущен и виден: выполните в cmd «adb devices» — одно устройство в статусе device. "
                    + "3) ANDROID_HOME задан и указывает на папку Sdk. "
                    + "4) Если устройств несколько — укажите deviceName в capabilities или отключите лишние.", e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания сессии Appium", e);
        } finally {
            executor.shutdownNow();
        }

        if (skipLaunch) {
            System.out.println("[BaseMobileTest] Открытие приложения через activateApp: " + appPackage);
            driver.activateApp(appPackage);
            // Даём приложению и UiAutomator2 на устройстве стабилизироваться (снижает риск "instrumentation process crashed")
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        // Неявное ожидание при поиске элементов (в Appium по умолчанию 0 ms — поиск сразу падает)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        System.out.println("[BaseMobileTest] Сессия создана, регистрация в Selenide");
        WebDriverRunner.setWebDriver(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownAndroidDriver() {
        if (driver != null) {
            String currentPackage = driver.getCurrentPackage();
            if (currentPackage != null && !isSystemPackage(currentPackage)) {
                driver.terminateApp(currentPackage);
            }
            ((HasNetworkConnection) driver)
                    .setConnection(new ConnectionStateBuilder()
                            .withWiFiEnabled()
                            .withDataEnabled()
                            .build());
            driver.quit();

        }
    }

    private boolean isSystemPackage(String packageName) {
        return packageName.startsWith("com.android.") || packageName.contains("launcher") || packageName.equals("com.google.android.apps.nexuslauncher");
    }
}

