package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.model.Project;
import com.example.teamcity.ui.admin.CreateProjectPage;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;

@Tag("regression")
public class CreateProjectTest extends BaseUiTest {

    private static final String REPO_URL = "https://github.com/SubbotinMXM/qa-project-lordfilm";

    @Test
    @Description("User should be able to create project")
    @Tag("positive")
    public void userCreatesProject() {
        // Подготовка окружения, поэтому и не проверяем корректность создания юзера. Не в этом цель этого теста
        step("Login as user");
        loginAsNewUser();

        // Взаимодействие с UI
        step("Open 'Create project page' (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository url)");
        step("Click 'proceed'");
        step("Fix Project name and Build type values");
        step("Click 'proceed'");
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // Проверка состояния API
        // (корректность отправки данных с UI на API)
        step("Check that all entities (project, build type) were successfully created with correct data on API level");
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS)
                .read("name:" + testData.getProject().getName());
        softy.assertThat(createdProject).isNotNull();
        TestDataStorage.getStorage().addCreatedEntity(Endpoint.PROJECTS, createdProject);

        // Проверка состояния UI
        // (корректность считывания данных и отображения данных на UI)
        step("Check that project is visible on project page (http://localhost:8111/favorite/projects)");
        ProjectPage.open(createdProject.getId()).title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var projectExists = ProjectsPage.open().getProjects().stream()
                .anyMatch(x -> x.getName().text()
                        .equals(testData.getProject().getName()));
        softy.assertThat(projectExists).isTrue();
    }

    @Test
    @Description("User should not be able to create project without name")
    @Tag("negative")
    public void userCreatesProjectWithoutName() {
        // Подготовка окружения, поэтому и не проверяем корректность создания юзера. Не в этом цель этого теста
        step("Login as user");
        step("Check number of projects");

        // Взаимодействие с UI
        step("Open 'Create project page' (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository url)");
        step("Click 'proceed'");
        step("Set empty name value");
        step("Click 'proceed'");

        // Проверка состояния API
        // (корректность отправки данных с UI на API)
        step("Check that amount of projects did not change");

        // Проверка состояния UI
        // (корректность считывания данных и отображения данных на UI)
        step("Check that error appears 'Project name must not be empty'");
    }
}
