package org.skypro.star.controller;

import org.skypro.star.service.RecommendationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RecommendationController {

    private final RecommendationRepository recommendationRepository;

    public RecommendationController(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    //test
    @GetMapping("/test/get/{userId}")
    public int getRandomTransactionAmountTest(@PathVariable UUID userId) {
        return recommendationRepository.getRandomTransactionAmount(userId);
    }

}
