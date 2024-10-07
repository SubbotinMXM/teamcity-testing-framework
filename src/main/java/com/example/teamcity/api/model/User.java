package com.example.teamcity.api.model;

import com.example.teamcity.api.annotations.Random;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseModel{
    private String id;

    @Random
    private String username;

    @Random
    private String password;

    private Roles roles;

}
