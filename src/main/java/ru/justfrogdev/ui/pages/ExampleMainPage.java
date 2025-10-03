package ru.justfrogdev.ui.pages;

import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class ExampleMainPage extends LoadableComponent<ExampleMainPage> {

    private static final Logger log = LogManager.getLogger(ExampleMainPage.class);
    private final WebDriver driver;
    private final String url;

    public ExampleMainPage(WebDriver driver, String url) {
        this.driver = driver;
        this.url = url;
        log.info("ExampleMainPage: {}", "ExampleMainPage");
        PageFactory.initElements(driver, this);
    }

    private final By newBy = By.xpath("Ещё xpath");
    private By button(String title) {
        return By.xpath("//a[text() = '" + title + "']");
    }

    @Override
    @Step("Открытие SignIn страницы")
    protected void load() {
        driver.get(url);
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.visibilityOfElementLocated(button("Картинки"))
        );
    }

    @Override
    protected void isLoaded() throws Error {
        assertThat(driver.getCurrentUrl(), containsString(url));
        assertThat(driver.findElement(button("Картинки")).isDisplayed(), is(true));
    }

    @Step("Вкладка Form Fields")
    public ExampleNewPage openFormFields() {
        driver.findElement(button("Картинки")).click();
        return new ExampleNewPage(driver);
    }
}
