package com.example.teamcity.api;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.model.BuildType;
import com.example.teamcity.api.model.Project;
import com.example.teamcity.api.model.Roles;
import com.example.teamcity.api.model.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
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
                .getRequest(USERS).create(testData.getUser()); // добавляется эндпоинт и отправляется запрос на создание юзера.
        // В testData уже лежат сгенерированные данные. Нужно только их подложить в запрос
        // По умолчанию созданному юзеру присваивается роль SYSTEM_ADMIN

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser())); // Формируется url с авторизационными данными и хостом
        userCheckRequests.<Project>getRequest(PROJECTS) // добавляется эндпоинт для созданя проекта
                .create(testData.getProject()); // а здесь отправляется запрос и подкладывается тело для создания проекта

        userCheckRequests.getRequest(BUILD_TYPES) // формируется url на другой эндпоинт, но с той же спекой на авторизацию по юзеру что и выше
                .create(testData.getBuildType()); // отправляется запрос и подкладывается тело на создание buildType

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES)
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
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()),
                BuildType.class, testData.getBuildType().getId());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
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
        step("Create project");
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        step("Create user-admin for project");
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        step("Create buildType by user-admin for project");
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var createdBuildTypeId = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType()).getId();

        step("Read created buildType");
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(createdBuildTypeId);

        step("Assertions");
        softy.assertThat(testData.getBuildType().getName())
                .withFailMessage("Build type name is not correct")
                .isEqualTo(createdBuildType.getName());
        softy.assertThat(testData.getBuildType().getId())
                .withFailMessage("Project for build type is not correct")
                .isEqualTo(createdBuildType.getId());
    }

    @Test
    @DisplayName("Project admin should not be able to create build type for not their project")
    @Tag("Negative")
    @Tag("Roles")
    void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        step("Create project1");
        superUserCheckRequests.getRequest(PROJECTS).create(testData.getProject());

        step("Create user1 with PROJECT_ADMIN role for project1");
        testData.getUser().setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + testData.getProject().getId()));
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());

        step("Create project2");
        var project2 = generate(Project.class);
        superUserCheckRequests.getRequest(PROJECTS).create(project2);

        step("Create user2 with PROJECT_ADMIN role for project2");
        var user2 = generate(User.class);
        user2.setRoles(generate(Roles.class, "PROJECT_ADMIN", "p:" + project2.getId()));
        superUserCheckRequests.getRequest(USERS).create(user2);

        step("Create buildType for project1 by user2 and check code 403");
        var buildType = generate(Arrays.asList(testData.getProject()), BuildType.class);
        var userUncheckedRequest = new UncheckedRequests(Specifications.authSpec(user2));
        userUncheckedRequest.getRequest(BUILD_TYPES)
                .create(buildType)
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(("You do not have enough permissions to edit project with id: %s\n" +
                                               "Access denied. Check the user has enough permissions to perform the operation.")
                        .formatted(testData.getProject().getId())));
    }
}
