package ui.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.justfrogdev.utils.Singleton;
import ru.justfrogdev.utils.models.UiConfig;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseUiTests extends AbstractTestNGSpringContextTests {

    private static final Logger log = LogManager.getLogger(BaseUiTests.class);

    protected WebDriver driver;
    protected UiConfig configuration = Singleton.getEnvironmentConfig().ui();

    @BeforeMethod
    @Step("Запуск ChromeDriver")
    public void startChromeDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        WebDriverManager.chromedriver().setup();
        
        // Включаем headless если не установлена переменная HEADED=true
        boolean isHeaded = Boolean.parseBoolean(System.getProperty("headed", "false"));
        if (!isHeaded) {
            chromeOptions.addArguments("--headless=new");  // Новый headless режим Chrome
        }
        
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");  // Стабильность в Docker/CI
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--incognito");
        chromeOptions.setExperimentalOption("w3c", true);
        chromeOptions.addArguments("--remote-allow-origins=*");
        
        // Ускорение: отключаем ненужные фичи
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--disable-logging");
        chromeOptions.addArguments("--log-level=3");  // Минимум логов
        
        // Опционально: отключение картинок для максимальной скорости
        // Включать только если тесты не проверяют изображения
        boolean disableImages = Boolean.parseBoolean(System.getProperty("disableImages", "false"));
        if (disableImages) {
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("profile.managed_default_content_settings.images", 2);  // 2 = блокировать
            chromeOptions.setExperimentalOption("prefs", prefs);
            chromeOptions.addArguments("--blink-settings=imagesEnabled=false");
        }
        
        driver = new ChromeDriver(chromeOptions);
    }

    @AfterMethod(alwaysRun = true)
    @Step("Остановка ChromeDriver")
    public void closeChromeDriver(ITestResult testResult) {
        if (driver == null) {
            log.warn("Driver is null");
            return;
        }

        if (!testResult.isSuccess()) {
            log.info("Test failed, going to take screenshot");
            takeScreenshot();
        }

        log.info("Closing driver");
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            driver.switchTo().window(handle);
            driver.close();
        }
        driver.quit();
    }

    @Attachment(value = "Screenshot", type = "image/png")
    protected byte[] takeScreenshot() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
