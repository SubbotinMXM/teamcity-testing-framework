package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.model.BuildType;
import com.example.teamcity.api.model.Project;
import com.example.teamcity.api.model.Roles;
import com.example.teamcity.api.model.User;
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
        superUserCheckRequests // в эту одну переменную сразу вынесено формирование url для авторизации супер юзером (без эндпоинта)
                .getRequest(Endpoint.USERS).create(testData.getUser()); // добавляется эндпоинт и отправляется запрос на создание юзера.
        // В testData уже лежат сгенерированные данные. Нужно только их подложить в запрос
        // По умолчанию созданному юзеру присваивается роль SYSTEM_ADMIN

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser())); // Формируется url с авторизационными данными и хостом
        userCheckRequests.<Project>getRequest(Endpoint.PROJECTS) // добавляется эндпоинт для созданя проекта
                .create(testData.getProject()); // а здесь отправляется запрос и подкладывается тело для создания проекта

        userCheckRequests.getRequest(Endpoint.BUILD_TYPES) // формируется url на другой эндпоинт, но с той же спекой на авторизацию по юзеру что и выше
                .create(testData.getBuildType()); // отправляется запрос и подкладывается тело на создание buildType

        var createdBuildType = userCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES)
                .read(testData.getBuildType().getId()); // То же самое что выше, только тут еще сохраняем по id созданные
        // buildType и не создаем его, а делаем запрос на получение его по id

        softy.assertThat(testData.getBuildType().getName())
                .withFailMessage("Build type name is not correct")
                .isEqualTo(createdBuildType.getName());
    }

    @Test
    @DisplayName("User should not be able to create two build types with the same id")
    @Tag("Negative")
    @Tag("CRUD")
    void userCreatesTwoBuildTypesWithTheSameIdTest() {
        // Суперюзером создали нового юзера
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
        var createdUser = superUserCheckRequests.<User>getRequest(Endpoint.USERS).create(testData.getUser());

        step("Create project by user");
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        step("Grant user PROJECT_ADMIN role in project");
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.<User>getRequest(Endpoint.USERS).update(createdUser.getId(), testData.getUser());

        step("Create buildType for project by user (PROJECT_ADMIN)");
        userCheckRequests.getRequest(Endpoint.BUILD_TYPES).create(testData.getBuildType());

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
