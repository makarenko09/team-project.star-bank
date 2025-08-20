package org.skypro.star.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Recommendation {
    private final String name;
    private final UUID id;
    private final String text;

    @Override
    public String toString() {
        return "\n \n" + name + '\n' +
                text + '\n';
    }
}