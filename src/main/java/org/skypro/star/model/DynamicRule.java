package org.skypro.star.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRule {

    private String query;
    private boolean negate;
    private List<String> arguments;
}