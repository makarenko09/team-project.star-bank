package org.skypro.star.service;

import org.skypro.star.repository.TransactionRepository;

import java.util.UUID;

public interface AcceptKeyForRecommendation {
        boolean checkRule(UUID userId, TransactionRepository transactionRepository);
}
