package ui.tests;

import io.qameta.allure.*;
import org.springframework.test.context.ContextConfiguration;
import ru.justfrogdev.listeners.ApiConfigurationListener;
import ru.justfrogdev.listeners.GroupIntersectionInterceptor;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.justfrogdev.ui.pages.ExampleNewPage;
import ru.justfrogdev.ui.pages.ExampleMainPage;
import ru.justfrogdev.utils.CLF;
import ru.justfrogdev.utils.RetryAnalyzer;
import ru.justfrogdev.utils.StepsConfiguration;

@Listeners({ApiConfigurationListener.class, GroupIntersectionInterceptor.class})
@ContextConfiguration(classes = StepsConfiguration.class)
@Epic("Допустим Компания ТестерОРГ")
@Feature("Допустим страница главная")
@TmsLink("Задача в JIRA")
@Issue("ID страницы в CF")
@Test(priority = 1,
        testName = "Наименование теста",
        groups =
                {
                        "Группа 1",
                        "Группа 2"
                }, retryAnalyzer = RetryAnalyzer.class)
public class ExampleTests extends BaseUiTests {

    @Test
    @Description("Название теста")
    public void test() {
        CLF.logI("Вход в приложение");
        ExampleMainPage mainPage = new ExampleMainPage(driver, configuration.url());
        CLF.logI("Открытие FormFields");
        ExampleNewPage newPage = mainPage.get().openFormFields();
    }
}

