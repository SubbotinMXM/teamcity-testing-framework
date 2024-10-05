package com.example.teamcity.api.spec;

import com.example.teamcity.api.model.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import static com.example.teamcity.api.config.Config.getConfig;
import static io.restassured.http.ContentType.JSON;

public class Specifications {

//    private static Specifications spec;

    private static RequestSpecBuilder reqBuilder() {
        var requestBuilder = new RequestSpecBuilder();
        requestBuilder.addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addHeader("Accept", "application/json")
                .build();
        return requestBuilder;
    }

    public static RequestSpecification superUserSpec(){
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://:%s@%s".formatted(
                getConfig().getProperty("superUserToken"),
                getConfig().getProperty("host")));
        return requestBuilder.build();
    }

    public static RequestSpecification unAuthSpec() {
        var requestBuilder = reqBuilder();
        return requestBuilder.setContentType(JSON)
                .setAccept(JSON)
                .build();
    }

    public static RequestSpecification authSpec(User user) {
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://%s:%s@%s".formatted(
                        user.getUsername(),
                        user.getPassword(),
                        getConfig().getProperty("host"))
                )
                .setAccept(JSON)
                .setContentType(JSON);
        return requestBuilder.build();
    }
}
