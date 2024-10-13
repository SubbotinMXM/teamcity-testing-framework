package com.example.teamcity.api.enums;

import com.example.teamcity.api.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.management.relation.RoleStatus;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class),
    PROJECTS("/app/rest/projects", Project.class),
    USERS("/app/rest/users", User.class);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
