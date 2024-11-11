package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.BuildTypeElement;
import com.example.teamcity.ui.elements.ProjectElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class ProjectsPage extends BasePage{
    public static final String PROJECTS_URL = "/favorite/projects";
    private ElementsCollection projectsElements = $$("div[class*='Subproject__container--Px']");

    public ProjectsPage() {
        header.shouldBe(Condition.visible);
    }

    private SelenideElement header = $("span[class*='ProjectPageHeader']");

    public static ProjectsPage open(){
        return Selenide.open(PROJECTS_URL, ProjectsPage.class);
    }

    public List<ProjectElement> getProjects(){
        return generatePageElements(projectsElements, ProjectElement::new);
    }

//    public ProjectsPage clickOnSearchableProject(String projectName){
//        $x("//div[contains(@class,'MainPanel')]//span[text()='" + projectName + "']").click();
//        return this;
//    }
}
