package org.skypro.star.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationAnswerUser {
    private final String user_id;
    private final List<Recommendation> recommendations;
}
