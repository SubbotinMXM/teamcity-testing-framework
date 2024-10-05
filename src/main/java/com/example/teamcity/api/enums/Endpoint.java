package com.example.teamcity.api.enums;

import com.example.teamcity.api.model.BaseModel;
import com.example.teamcity.api.model.BuildType;
import com.example.teamcity.api.model.Project;
import com.example.teamcity.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class),
    PROJECTS("/app/rest/projects", Project.class),
    USERS("/app/rest/users", User.class);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
