package com.example.teamcity.api.spec;

import com.example.teamcity.api.model.User;
import com.example.teamcity.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

import static com.example.teamcity.config.Config.*;
import static io.restassured.http.ContentType.JSON;

public class Specifications {

    private static Specifications spec;

    private Specifications(){}

    public static Specifications getSpec(){
        if (spec == null){
            spec = new Specifications();
        }
        return spec;
    }

    private RequestSpecBuilder reqBuilder(){
        var requestBuilder = new RequestSpecBuilder();
         requestBuilder.addFilter(new ResponseLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
        return requestBuilder;
    }

    public RequestSpecification unAuthSpec(){
        var requestBuilder = reqBuilder();
        return requestBuilder.setContentType(JSON)
                .setAccept(JSON)
                .build();
    }

    public RequestSpecification authSpec(User user){
        var requestBuilder = reqBuilder();
        requestBuilder.setBaseUri("http://" + user.getUsername() + ":" + user.getPassword() + "@" +
                                  getConfig().getProperty("host"))
                .setAccept(JSON)
                .setContentType(JSON);
        return requestBuilder.build();
    }
}
