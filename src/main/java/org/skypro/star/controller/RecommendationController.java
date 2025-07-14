package org.skypro.star.controller;

import org.skypro.star.model.RecommendationAnswer;
import org.skypro.star.repository.TransactionRepository;
import org.skypro.star.service.RecommendationRuleSetImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/recommendation")
public class RecommendationController {
    private final TransactionRepository transactionRepository;
    private final RecommendationRuleSetImpl recommendationRuleSet;

    private final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    public RecommendationController(TransactionRepository transactionRepository, RecommendationRuleSetImpl recommendationRuleSet) {
        this.transactionRepository = transactionRepository;
        this.recommendationRuleSet = recommendationRuleSet;
    }

    @GetMapping("/{user_id}")
    public RecommendationAnswer getRecommendation(@PathVariable(name = "user_id") UUID user_id) {
        return recommendationRuleSet.getRecommendation(UUID.fromString(user_id.toString()));

    }

    // TODO - DEV REQUEST
    @GetMapping
    public void rulesData() {
        recommendationRuleSet.rulesData();
    }

    @GetMapping("/get/amount-of-transaction/{userId}")
    public int countTransactionByUserId(@PathVariable UUID userId) {
        return transactionRepository.countTransaction(userId);
    }

    @GetMapping("/get/amount-of-transaction-by-productType")
    public int countTransactionByUserIdAndProductType(@RequestParam("userId") UUID userId, @RequestParam("productType") String productType) {
        return transactionRepository.countTransactionByProductType(userId, productType);
    }

    @GetMapping("/get/check-of-present-product-by-productType")
    public boolean findAptTypeProductByUserIdAndProductType(@RequestParam("userId") UUID userId, @RequestParam("productType") String productType) {
        return transactionRepository.findAptTypeProductByProductType(userId, productType);
    }

    @GetMapping("/get/compare-of-current-and-apt-sum-of-amount-transactions")
    public boolean findCurrentSumDepositMoreThatAptSumByUserIdAndProductTypeAndAmount(@RequestParam("userId") UUID userId, @RequestParam("productType") String productType, @RequestParam("sum") Integer sum, @RequestParam("equels") Boolean equels) {
        return transactionRepository.findCurrentSumDepositsMoreThatAptSumOrAndEqualsByProductTypeAndAmount(userId, productType, sum, equels);
    }

    @GetMapping("/get/compare-of-deposit-and-withdraw-sum-of-amount-transactions")
    public boolean findSumMoreThatByTransactionTypeAndProductType(@RequestParam("userId") UUID userId, @RequestParam("productType") String productType) {
        return transactionRepository.findSumMoreThatByTransactionTypeAndProductType(userId, productType);
    }

}
