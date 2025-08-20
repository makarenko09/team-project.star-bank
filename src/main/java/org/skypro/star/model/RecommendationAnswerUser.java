package org.skypro.star.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationAnswerUser {
    private final String userId;
    private final List<Recommendation> recommendations;

    @Override
    public String toString() {
        return "\n" + recommendations + "\n";
    }
}
