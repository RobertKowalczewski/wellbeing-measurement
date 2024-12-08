package com.example.projectmatrix.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SmartwatchDataMapper {

    public static SmartwatchDataDto map(String json) {
        try {
            var parsedJson = new ObjectMapper().readTree(json);
            return new SmartwatchDataDto(
                    parsedJson.get("heartRate").asDouble()
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid json was provided");
        }
    }
}
