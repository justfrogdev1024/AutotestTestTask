package ru.justfrogdev.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.justfrogdev.ui.utils.UIWaitHelper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class AbstractPage {

    protected final WebDriver driver;
    protected final UIWaitHelper wait;
    @Deprecated
    protected final WebDriverWait driverWait;

    protected AbstractPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new UIWaitHelper(driver);
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected void elementAwait(By by) {
        wait.waitForVisible(by);
        wait.waitForClickable(by);
    }

    protected void elementAwait(By by, Duration customTimeout) {
        wait.waitForVisible(by, customTimeout);
        wait.waitForClickable(by, customTimeout);
    }

    protected void elementVisibilityAwait(By by) {
        wait.waitForVisible(by);
    }

    protected void elementVisibilityAwait(By by, Duration customTimeout) {
        wait.waitForVisible(by, customTimeout);
    }

    protected void noElementAwait(By by) {
        wait.waitForInvisible(by);
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
        // Проверяем текущее состояние
        if (!driver.findElements(targetBy).isEmpty() 
            && driver.findElement(targetBy).isDisplayed()) {
            return; // Уже в нужном состоянии
        }

        // Используем оптимизированный метод из UIWaitHelper
        wait.clickUntilStateChanges(labelBy, targetBy, 3);
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
