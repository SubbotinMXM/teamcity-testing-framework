package com.example.teamcity.api;

import com.example.teamcity.api.model.User;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static com.example.teamcity.api.spec.Specifications.*;
import static io.restassured.RestAssured.given;

public class BuildConfigurationTest extends BaseApiTest{

    @Test
    void buildConfigurationTest(){
        var user = User.builder()
                .username("admin")
                .password("admin")
                .build();

        String token = given()
                .spec(getSpec().authSpec(user))
                .get("/authenticationTest.html?csrf")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract().asString();

        System.out.println(token);
    }
}
