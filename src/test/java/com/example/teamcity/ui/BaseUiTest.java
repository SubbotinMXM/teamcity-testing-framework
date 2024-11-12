package com.example.teamcity.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.example.teamcity.BaseTest;
import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.ui.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

public class BaseUiTest extends BaseTest {

    @BeforeAll
    public static void setupUiTest() {
        Configuration.browser = Config.getConfig().getProperty("browser");
        Configuration.baseUrl = "http://" + Config.getConfig().getProperty("host");
        // НЕ ПИСАТЬ ТЕСТЫ НА ЛОКАЛЬНОМ БРАУЗЕРЕ!!! А ТО ПОТОМ ПРИ УДАЛЕННОМ ЗАПУСКЕ ПРИДЕТСЯ ВСЕ ПЕРЕПИСЫВАТЬ
        Configuration.remote = Config.getConfig().getProperty("remote");
        Configuration.browserSize = Config.getConfig().getProperty("browserSize");
        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true,
                        "enableLog", true));
        Configuration.pageLoadStrategy = "eager";
        Configuration.timeout = 30000;
        Configuration.pageLoadTimeout = 30000;

//        System.setProperty("webdriver.chrome.driver", "/Users/maksimsubbotin/Documents/Programm/chromedriver");
    }

    @AfterEach
    public void closeWebDriver(){
        Selenide.closeWebDriver();
    }

    protected void loginAsNewUser(){
        superUserCheckRequests.getRequest(Endpoint.USERS).create(testData.getUser());
        LoginPage.open().login(testData.getUser());
    }
}
