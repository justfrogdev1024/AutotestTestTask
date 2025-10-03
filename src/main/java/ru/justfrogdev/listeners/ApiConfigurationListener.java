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

import static io.restassured.RestAssured.given;

public class ApiConfigurationListener implements IExecutionListener, ITestListener, IInvokedMethodListener {

    private static void init() {
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
    public void onTestSuccess(ITestResult result) {
        RestAssured.reset();
        init();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        RestAssured.reset();
        init();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        RestAssured.reset();
        init();
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        RestAssured.reset();
        init();
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        if (method.isTestMethod()) {
            Allure.getLifecycle().updateTestCase(result -> {
                result.setName(testResult.getName());
            });
        }
    }
}
