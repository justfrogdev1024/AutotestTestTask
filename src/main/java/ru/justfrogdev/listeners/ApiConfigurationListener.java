package ru.justfrogdev.listeners;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import org.testng.*;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.restassured.RestAssured.given;

public class ApiConfigurationListener implements IExecutionListener, ITestListener, IInvokedMethodListener {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private static void init() {
        if (INITIALIZED.getAndSet(true)) {
            return;
        }

        RestAssured.config = RestAssured.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory(
                        (type, charset) -> JsonMapper.builder()
                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                .addModule(new JavaTimeModule()
                                        .addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer()))
                                .build()
                ));
        RestAssured.requestSpecification = given().filters(
                new AllureRestAssured()
        );
    }

    @Override
    public void onExecutionStart() {
        init();
    }

    @Override
    public void onTestSuccess(ITestResult result) {}

    @Override
    public void onTestFailure(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {}

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        if (method.isTestMethod()) {
            Allure.getLifecycle().updateTestCase(result -> {
                result.setName(testResult.getName());
            });
        }
    }
}
