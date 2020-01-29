package com.damaya.kalah.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameResponse {

    private String id;
    private String url;
    private Map<String, String> status;
}
