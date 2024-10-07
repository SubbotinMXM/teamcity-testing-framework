package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.model.BuildType;
import com.example.teamcity.api.model.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Tag("Regression")
public class BuildTypeTest extends BaseApiTest {

    @Test
    @DisplayName("User should be able to create build type")
    @Tag("Positive")
    @Tag("CRUD")
    void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(Endpoint.USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        userCheckRequests.getRequest(Endpoint.BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES)
                .read(testData.getBuildType().getId());

        softy.assertThat(testData.getBuildType().getName())
                .withFailMessage("Build type name is not correct")
                .isEqualTo(createdBuildType.getName());
    }

    @Test
    @DisplayName("User should not be able to create two build types with the same id")
    @Tag("Negative")
    @Tag("CRUD")
    void userCreatesTwoBuildTypesWithTheSameIdTest() {
        superUserCheckRequests.getRequest(Endpoint.USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()),
                BuildType.class, testData.getBuildType().getId());

        userCheckRequests.getRequest(Endpoint.BUILD_TYPES).create(testData.getBuildType());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), Endpoint.BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(
                        ("The build configuration / template ID \"%s\" is already used by another configuration or template")
                        .formatted(testData.getBuildType().getId())));
    }

    @Test
    @DisplayName("Project admin should be able to create build type for their project")
    @Tag("Positive")
    @Tag("Roles")
    void projectAdminCreatesBuildTypeTest() {
        step("Create user");
        step("Create project by user");
        step("Grant user PROJECT_ADMIN role in project");
        step("Create buildType for project by user (PROJECT_ADMIN)");
        step("Check buildType was created successfully");
    }

    @Test
    @DisplayName("Project admin should not be able to create build type for not their project")
    @Tag("Negative")
    @Tag("Roles")
    void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        step("Create user1");
        step("Create project by user1");
        step("Grant user PROJECT_ADMIN1 role in project1");

        step("Create user2");
        step("Create project by user2");
        step("Grant user PROJECT_ADMIN2 role in project2");

        step("Create buildType for project1 by user2");
        step("Check buildType was not created with bad request code");
    }
}
