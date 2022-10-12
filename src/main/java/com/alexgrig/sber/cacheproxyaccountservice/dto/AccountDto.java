package com.alexgrig.sber.cacheproxyaccountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AccountDto {
}

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Post {
    @JsonProperty("userId")
    int userId;
    @JsonProperty("id")
    int id;
    @JsonProperty("title")
    String title;
    @JsonProperty("body")
    String body;
}
