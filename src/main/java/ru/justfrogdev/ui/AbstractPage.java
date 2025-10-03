package ru.justfrogdev.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class AbstractPage {

    protected final WebDriver driver;
    protected final WebDriverWait driverWait;

    protected AbstractPage(WebDriver driver) {
        this.driver = driver;
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected void elementAwait(By by) {
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        driverWait.until(ExpectedConditions.elementToBeClickable(by));
    }

    protected void elementVisibilityAwait(By by) {
        driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void noElementAwait(By by) {
        driverWait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    protected void click(By by) {
        elementAwait(by);

        driver.findElement(by).click();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void doubleClick(By by) {
        elementAwait(by);

        new Actions(driver).doubleClick(driver.findElement(by)).perform();
    }

    protected void clear(By by) {
        elementAwait(by);

        driver.findElement(by).clear();
    }

    protected String clearBySymbol(int count) {
        StringBuilder symbolCount = new StringBuilder();
        for (int i = 0; i < count; i++) {
            symbolCount.append(Keys.BACK_SPACE);
        }
        return symbolCount.toString();
    }

    protected void input(By by, String input) {
        elementAwait(by);

        driver.findElement(by).sendKeys(input);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getText(By by) {
        elementAwait(by);

        return driver.findElement(by).getText();
    }

    protected void clickCheckbox(By labelBy, By targetBy) {
        elementAwait(labelBy);

        if (driver.findElements(targetBy).isEmpty()) {
            int attempts = 0;
            final int maxAttempts = 4;
            while (attempts < maxAttempts) {
                try {
                    driver.findElement(labelBy).click();
                    attempts++;
                    new WebDriverWait(driver, Duration.ofMillis(200)).until(
                            ExpectedConditions.visibilityOfElementLocated(targetBy)
                    );
                    return;

                } catch (NoSuchElementException | TimeoutException ignored) {
                }
            }

            throw new RuntimeException("У checkbox'а " + labelBy + " нет состояния " + targetBy);
        }
    }

    protected void clickCheckbox(By by, boolean state) {
        elementAwait(by);

        if (!driver.findElement(by).isSelected() && state) {
            driver.findElement(by).click();
        } else if (driver.findElement(by).isSelected() && !state) {
            driver.findElement(by).click();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected Boolean checkExists(By by) {
        try {
            return driver.findElement(by).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
