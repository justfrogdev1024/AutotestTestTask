package ru.justfrogdev.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

public class CLF implements Filter { //CustomLoggingFilter
    private static final boolean LOGGING_ENABLED = Boolean.parseBoolean(System.getProperty("enableLogging", "true"));

    private static final Logger log = LogManager.getLogger(CLF.class);

    private final PrintStream stream;

    public CLF(PrintStream stream) {
        this.stream = stream;
    }

    public static boolean isLoggingEnabled() {
        return LOGGING_ENABLED;
    }

    private static String getTestClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (className.contains("Tests")) {
                return className;
            }
        }
        return "<unknown-test-class>";
    }

    @SneakyThrows
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        log.info("TestName: {}", getTestClassName());

        log.info("Request method: {}", requestSpec.getMethod());
        log.info("Request URI: {}", requestSpec.getURI());
        Object requestBody = requestSpec.getBody();
        if (requestBody != null) {
            ObjectMapper objectMapper = Singleton.getJsonObjectMapper();
            String prettyBody = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(
                            objectMapper.readTree(requestBody.toString())
                    );
            log.info("Request Body: {}", prettyBody);
        } else {
            log.info("Request Body: <none>");
        }

        Response response = ctx.next(requestSpec, responseSpec);
        log.info("Response Status Code: {}", response.getStatusCode());
        String responseBody = response.getBody().asPrettyString();
        log.info("Response Body: {}", responseBody);

        return response;
    }

    public static void logI(String message, Object... args) {
        if (CLF.isLoggingEnabled()) {
            log.info(message, args);
        }
    }

    public static void logE(String message, Object... args) {
        if (CLF.isLoggingEnabled()) {
            log.error(message, args);
        }
    }

    public static void logD(String message, Object... args) {
        if (CLF.isLoggingEnabled()) {
            log.debug(message, args);
        }
    }
}