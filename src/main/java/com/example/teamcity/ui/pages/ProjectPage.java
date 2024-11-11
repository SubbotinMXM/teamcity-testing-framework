package com.example.teamcity.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.BuildTypeElement;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectPage extends BasePage{
    private static final String PROJECT_URL = "/project/%s";
    public SelenideElement title = $("span[class*='ProjectPageHeader']");
    private ElementsCollection buildTypesElements = $$("div[class*='Builds__']");

    public static ProjectPage open(String projectId){
        return Selenide.open(PROJECT_URL.formatted(projectId), ProjectPage.class);
    }

    public List<BuildTypeElement> getBuildTypes(){
        buildTypesElements.shouldHave(sizeGreaterThan(0));
        return generatePageElements(buildTypesElements, BuildTypeElement::new);
    }
}
