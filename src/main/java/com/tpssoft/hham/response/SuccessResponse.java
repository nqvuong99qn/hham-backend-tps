package com.tpssoft.hham.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class SuccessResponse {
    private final Map<String, Object> values = new HashMap<>();


    @JsonAnyGetter
    public Map<String, Object> getValues() {
        return values;
    }

    @JsonAnySetter
    public SuccessResponse put(String key, Object value) {
        values.put(key, value);
        return this;
    }
}
