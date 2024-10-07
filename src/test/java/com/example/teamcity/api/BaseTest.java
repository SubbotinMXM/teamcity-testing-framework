package com.example.teamcity.api;

import com.example.teamcity.api.generators.TestDataStorage;
import com.example.teamcity.api.model.TestData;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.example.teamcity.api.generators.TestDataGenerator.generate;

public class BaseTest {

    protected TestData testData;

    protected CheckedRequests superUserCheckRequests = new CheckedRequests(Specifications.superUserSpec());

    protected SoftAssertions softy;

    @BeforeEach
    public void beforeTest(){
        softy = new SoftAssertions();
        testData = generate();
    }

    @AfterEach
    public void afterTest(){
        softy.assertAll();
        TestDataStorage.getStorage().deleteCreatedEntities();
    }
}
