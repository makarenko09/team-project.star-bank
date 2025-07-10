package org.skypro.star.controller;

import org.skypro.star.model.Recommendation;
import org.skypro.star.repository.RecommendationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RecommendationController {

    // those one method for teamProject
    public Recommendation getRecommendation(UUID id) {
        return null;
    }
}
