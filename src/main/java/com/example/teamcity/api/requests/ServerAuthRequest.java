package com.example.teamcity.api.requests;

import com.example.teamcity.api.model.ServerAuthSettings;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class ServerAuthRequest {
    private static final String SERVER_AUTH_SETTINGS_URL = "app/rest/server/authSettings";
    private RequestSpecification spec;

    public ServerAuthRequest(RequestSpecification spec) {
        this.spec = spec;
    }

    public ServerAuthSettings read() {
        return given()
                .spec(spec)
                .contentType(JSON)
                .get(SERVER_AUTH_SETTINGS_URL)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(ServerAuthSettings.class);

    }

    public ServerAuthSettings update(ServerAuthSettings authSettings) {
        return given()
                .spec(spec)
                .contentType(JSON)
                .body(authSettings)
                .put(SERVER_AUTH_SETTINGS_URL)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .extract().as(ServerAuthSettings.class);
    }
}
