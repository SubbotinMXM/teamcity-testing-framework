package com.example.teamcity.ui;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.model.BuildType;
import com.example.teamcity.api.model.Project;
import com.example.teamcity.ui.admin.CreateBuildTypePage;
import com.example.teamcity.ui.admin.CreateProjectPage;
import com.example.teamcity.ui.pages.ProjectPage;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("regression")
public class CreateBuildTypeTest extends BaseUiTest {

    private static final String REPO_URL = "https://github.com/SubbotinMXM/qa-project-lordfilm";

    @Test
    @Description("User should be able to create build type for existing project")
    @Tag("positive")
    public void userCreatesBuildType() {
        // Создаем юзера, логинимся
        loginAsNewUser();

        // Создание проекта
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // Проверка создания билда на уровне апи
        // Если не проверять сначала проект, то проверка апи билда периодически падает.
        // Если бы я сам выбирал инструменты, ты добавил бы сюда awaitility и отправлял бы запросы на проверку
        // билда несколько раз в цикле. Тогда проверку проекта можно было бы убрать
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECTS)
                .read("name:" + testData.getProject().getName());
        softy.assertThat(createdProject).isNotNull();
        TestDataStorage.getStorage().addCreatedEntity(Endpoint.PROJECTS, createdProject);

        var createdBuildType = superUserCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES)
                .read("name:" + testData.getBuildType().getName());
        softy.assertThat(createdBuildType).isNotNull();

        // Сроку ниже закомментил, тк она по сути не имеет смысла, потому что ранее чуть выше вызываем метод
        // по сохранению в сторэдж проекта. А проект включает в себя билд. А значит при удалении проекта в конце
        // теста билд удалится автоматом.
//        TestDataStorage.getStorage().addCreatedEntity(Endpoint.BUILD_TYPES, createdBuildType);

        // проверка создания билда на уровне ui
        var buildTypeExists = ProjectPage.open(createdProject.getId())
                .getBuildTypes().stream()
                .anyMatch(x -> x.getName().text().equals(testData.getBuildType().getName()));
        softy.assertThat(buildTypeExists).isTrue();
    }

    @Test
    @Description("User should not be able to create build type in Root project")
    @Tag("negative")
    public void userCreatesBuildTypeInRootProject() {
        // Создаем юзера, логинимся
        loginAsNewUser();

        // Пробуем создать билд тайп под проект _Root и проверяем ошибку
        CreateBuildTypePage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getBuildType().getName())
                .checkBuildTypeNameError();
    }
}
