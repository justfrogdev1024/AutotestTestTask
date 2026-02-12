package mobile.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.justfrogdev.listeners.GroupIntersectionInterceptor;
import ru.justfrogdev.utils.RetryAnalyzer;
import ru.justfrogdev.utils.StepsConfiguration;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.awaitility.Awaitility.await;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

@Listeners({GroupIntersectionInterceptor.class})
@ContextConfiguration(classes = StepsConfiguration.class)
@Epic("Допустим Компания ТестерОРГ")
@Feature("Допустим игра Alchemy")
@TmsLink("Задача в JIRA")
@Issue("ID страницы в CF")
@Test(testName = "Тест игры Alchemy",
        groups =
                {
                        "mobile",
                        "ui"
                }, retryAnalyzer = RetryAnalyzer.class)
public class AlchemyTest extends BaseMobileTest {
    private static final int EXPECTED_HINTS = 4;
    private static int realHints;
    private static SelenideElement hint = $(By.xpath("//android.widget.TextView[@text='Your hints']" +
            "/ancestor::android.view.View[1]//android.widget.TextView[@text!='Your hints']"));


    @Override
    protected String getAppPackage() {
        return "com.ilyin.alchemy";
    }

    @Override
    protected String getAppActivity() {
        return "com.ilyin.app_google_core.GoogleAppActivity";
    }

    @Test
    @Description("Играть → подсказки → подсказка за рекламу → проверка количества подсказок = 4")
    public void test() throws InterruptedException {

        gameEnter();

        checkHints();

        adWatchStart();

        adWatching();

        checkHintsAfterAds();

    }

    @Step("Вход в игру")
    private void gameEnter() {
        System.out.println("Клик на кнопку 'Play'");
        $(By.xpath("//y2.f1/android.view.View/android.view.View/android.view.View/android.view.View[5]/android.widget.Button"))
                .shouldBe(Condition.visible, Duration.ofSeconds(20))
                .click();
    }

    @Step("Открытие страницы подсказок и проверка счётчика подсказок")
    private void checkHints() {
        System.out.println("Нажатие на кнопку добавления подсказок");
        $(By.xpath("//y2.f1/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.view.View[2]"))
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();

        System.out.println("Проверка количества подсказок в начале (getText)");
        realHints = Integer.parseInt(hint.shouldBe(Condition.visible, Duration.ofSeconds(5)).getText());
        Assert.assertEquals(realHints, 2,
                "Количество подсказок не совпадает. Ожидалось: "
                        + 2 + ", фактически: " + realHints);
    }

    @Step("Запуск просмотра рекламы")
    private void adWatchStart() {
        System.out.println("Ожидание пока пропадёт загрузка");
        SelenideElement loading = $(By.xpath("//android.widget.TextView[@text='Loading']"));
        if (loading.exists()){
            loading.shouldBe(Condition.disappear, Duration.ofSeconds(20));
        }

        System.out.println("Есди рекламы нет - нажимаем на перепроверку наличия рекламы");
        SelenideElement noAds =
                $(By.xpath("//android.widget.TextView[@text='No ads']"));
        if (noAds.exists()) {
            System.out.println("Рекламы нет, пробуем ещё раз");
            noAds.click();
        }

        System.out.println("Нажатие на кнопку Watch");
        $(By.xpath("//android.widget.TextView[@text='Watch']"))
                .shouldBe(Condition.visible, Duration.ofSeconds(20))
                .click();
        $(By.xpath("//android.widget.TextView[@text='Watch']"))
                .shouldBe(Condition.disappear, Duration.ofSeconds(20));
    }

    @Step("Просмотр рекламы")
    private void adWatching() throws InterruptedException {
        System.out.println("Закрытие всех реклам");
        long end = System.currentTimeMillis() + Duration.ofSeconds(180).toMillis();
        By closeBtnLocator = By.id("com.ilyin.alchemy:id/bigo_ad_btn_close");
        while (System.currentTimeMillis() < end) {
            if (hint.exists()) {
                System.out.println("Элемент с подсказками появился!");
                break;
            } else if ($(closeBtnLocator).exists()) {
                Thread.sleep(1000);
                $(closeBtnLocator).click();
                System.out.println("Реклама закрыта");
            }
        }
    }

    @Step("Повторная проверка счётчика подсказое")
    private void checkHintsAfterAds() {
        System.out.println("Проверка количества подсказок в конце");
        realHints = Integer.parseInt(hint.shouldBe(Condition.visible, Duration.ofSeconds(5)).getText());
        Assert.assertEquals(realHints, EXPECTED_HINTS,
                "Количество подсказок не совпадает. Ожидалось: "
                        + EXPECTED_HINTS + ", фактически: " + realHints);
    }

    @AfterMethod
    public void clearApp() throws InterruptedException {
        Map<String, Object> args = Map.of(
                "command", "pm",
                "args", List.of("clear", "com.ilyin.alchemy")
        );
        driver.executeScript("mobile: shell", args);
        Thread.sleep(2000);
    }
}
