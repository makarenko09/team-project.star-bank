package org.skypro.star.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class DynamicRule {
    private final String query;
    @JsonProperty("arguments")
    private final String[] argumentsArray;
    @JsonProperty("negate")
    private final boolean isNegate;
}
