package ru.justfrogdev.utils;

import io.restassured.specification.RequestSpecification;
import ru.justfrogdev.utils.CLF;
import ru.justfrogdev.utils.models.ApiConfig;

import static io.restassured.RestAssured.given;

public final class RestClients {

    private RestClients() {
    }

    public static RequestSpecification api() {
        ApiConfig configuration = Singleton.getEnvironmentConfig().api();
        RequestSpecification spec = given()
                .baseUri(configuration.url())
                .auth().basic(configuration.username(), configuration.password());
        return withLogging(spec);
    }

    private static RequestSpecification withLogging(RequestSpecification spec) {
        if (CLF.isLoggingEnabled()) {
            return spec.filter(new CLF(System.out));
        }
        return spec;
    }
}