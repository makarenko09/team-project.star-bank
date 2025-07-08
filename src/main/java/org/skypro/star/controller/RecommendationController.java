package org.skypro.star.controller;

import org.skypro.star.repository.RecommendationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RecommendationController {

    private final RecommendationRepository recommendationRepository;

    public RecommendationController(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @GetMapping("/get/amount-of-transaction/{userId}")
    public int countTransactionByUserId(@PathVariable UUID userId) {
        return recommendationRepository.countTransaction(userId);
    }

    @GetMapping("/get/amount-of-transaction-by-ProductType")
    public int countTransactionByUserIdAndProductType(@RequestParam("userId") UUID userId, @RequestParam("productType") String productType) {
        return recommendationRepository.countTransactionByProductType(userId, productType);
    }

    @GetMapping("/get/check-of-present-Product")
    public boolean findAptTypeProductByUserIdAndProductType(@RequestParam("userId") UUID userId, @RequestParam("productType") String productType) {
        return recommendationRepository.findAptTypeProductByProductType(userId, productType);
    }

//    @GetMapping("/get/amount-of-transaction-by-ProductType/{userId}&{productType}")
//    public int countTransactionByUserIdAndProductType(@PathVariable UUID userId, @PathVariable String productType) {
//        return recommendationRepository.countTransactionByProductType(userId, productType);
//    }
}
