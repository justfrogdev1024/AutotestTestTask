package ru.justfrogdev.ui.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class UIWaitHelper {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public UIWaitHelper(WebDriver driver) {
        this(driver, Duration.ofSeconds(10));
    }

    public UIWaitHelper(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForTextToBe(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public WebElement waitForPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement waitForVisible(By locator, Duration customTimeout) {
        WebDriverWait customWait = new WebDriverWait(driver, customTimeout);
        return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator, Duration customTimeout) {
        WebDriverWait customWait = new WebDriverWait(driver, customTimeout);
        return customWait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void clickUntilStateChanges(By clickTarget, By expectedState, int maxAttempts) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofMillis(500));
        shortWait.pollingEvery(Duration.ofMillis(50));
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            waitForClickable(clickTarget).click();
            
            try {
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(expectedState));
                return;
            } catch (TimeoutException e) {
                if (attempt >= maxAttempts) {
                    throw new RuntimeException(
                        String.format("Элемент %s не достиг состояния %s за %d попыток",
                                    clickTarget, expectedState, maxAttempts)
                    );
                }
            }
        }
    }
}
