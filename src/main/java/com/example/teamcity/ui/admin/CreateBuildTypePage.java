package com.example.teamcity.ui.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForInterfaceTypes;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypePage extends CreateBasePage{
    private static final String PROJECT_SHOW_NODE = "createBuildTypeMenu";
    private SelenideElement buildTypeNameError = $("#error_buildTypeName");

    public CreateBuildTypePage createForm(String url){
        baseCreateForm(url);
        return this;
    }

    public static CreateBuildTypePage open(String projectId){
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_NODE), CreateBuildTypePage.class);
    }

    public CreateBuildTypePage setupProject(String buildTypeName){
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
        return this;
    }

    public void checkBuildTypeNameError(){
        buildTypeNameError.shouldBe(Condition.visible);
    }
}
