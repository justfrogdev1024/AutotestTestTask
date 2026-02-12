package mobile.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.android.connection.ConnectionStateBuilder;
import io.appium.java_client.android.connection.HasNetworkConnection;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.justfrogdev.listeners.ApiConfigurationListener;
import ru.justfrogdev.listeners.GroupIntersectionInterceptor;
import ru.justfrogdev.utils.CLF;
import ru.justfrogdev.utils.RetryAnalyzer;
import ru.justfrogdev.utils.StepsConfiguration;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.awaitility.Awaitility.await;

@Listeners({GroupIntersectionInterceptor.class})
@ContextConfiguration(classes = StepsConfiguration.class)
@Epic("Допустим Компания ТестерОРГ")
@Feature("Допустим приложение VK VIDEO")
@TmsLink("Задача в JIRA")
@Issue("ID страницы в CF")
@Test(testName = "Тест приложения VK Video",
        groups =
                {
                        "mobile",
                        "ui"
                }, retryAnalyzer = RetryAnalyzer.class)
public class VkVideoTest extends BaseMobileTest {

    private static SelenideElement videoCard =
            $(By.xpath("(//android.widget.ImageView[@resource-id=\"com.vk.vkvideo:id/preview\"])[1]"));

    @Override
    protected String getAppPackage() {
        return "com.vk.vkvideo";
    }

    @Override
    protected String getAppActivity() {
        return null;
    }

    @Override
    protected boolean createSessionWithoutLaunchingApp() {
        return true;
    }

    @Test
    @Description("Позитивный тест")
    public void testPositive() {

        boolean loaded = loadingChecker();

        Assert.assertTrue(loaded, "Видео не прогрузилось");
        videoCard.click();
        System.out.println("Видео нажалось");

        System.out.println("Проверка что видео открылось");
        $(By.xpath("//android.view.ViewGroup[@resource-id=\"com.vk.vkvideo:id/video_author_view\"]"))
                .shouldBe(Condition.visible, Duration.ofSeconds(20));
        System.out.println("Видео успешно открылось");

        exitVideo();
    }

    @Test(dependsOnMethods = "testPositive")
    @Description("Негативный тест")
    public void testNegative() throws InterruptedException {
        turnOffWifi();

        videoCard.click();
        System.out.println("Видео нажалось");

        System.out.println("Проверка что видео не грузится");
        By videoLoading = By.xpath("//android.widget.ProgressBar[@resource-id=\"com.vk.vkvideo:id/progress_view\"]");
        $(videoLoading)
                .shouldBe(Condition.visible, Duration.ofSeconds(20));

        System.out.println("Видео не грузится");


        exitVideo();

        turnOnWifi();
    }


    @Step("Ожидание загрузок")
    private boolean loadingChecker() {
        System.out.println("Ожидание прогрузки");
        By previewContainer = By.xpath(
                "(//android.widget.FrameLayout[@resource-id='com.vk.vkvideo:id/content'])[1]/android.view.ViewGroup"
        );
        $(previewContainer)
                .shouldBe(Condition.visible, Duration.ofSeconds(20));

        return videoCard.exists() && videoCard.isDisplayed();
    }

    @Step("Выключение wifi")
    private void turnOffWifi() {
        System.out.println("Выключение интернета");
        ((HasNetworkConnection) driver)
                .setConnection(new ConnectionStateBuilder().withWiFiDisabled().build());
    }

    @Step("Включение wifi")
    private void turnOnWifi() {
        System.out.println("Включение интернета");
        ((HasNetworkConnection) driver)
                .setConnection(new ConnectionStateBuilder()
                        .withWiFiEnabled()
                        .withDataEnabled()
                        .build());
    }

    @Step("Выключение видео")
    private void exitVideo() {
        System.out.println("Выключение видео");
        driver.pressKey(new KeyEvent(AndroidKey.BACK));
        $(By.xpath("//android.widget.ImageButton[@content-desc=\"Close\"]"))
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();
    }
}
