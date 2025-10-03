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
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--incognito");
//        chromeOptions.addArguments("--headless");
        chromeOptions.setExperimentalOption("w3c", true);
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
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
