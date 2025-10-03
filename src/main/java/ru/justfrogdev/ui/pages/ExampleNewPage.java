package ru.justfrogdev.ui.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import ru.justfrogdev.ui.AbstractPage;


public class ExampleNewPage extends AbstractPage {

    public ExampleNewPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        log.info("ExampleNewPage: {}", "ExampleNewPage");
        PageFactory.initElements(driver, this);
    }

    private static final Logger log = LogManager.getLogger(ExampleNewPage.class);
    private final WebDriver driver;
}
