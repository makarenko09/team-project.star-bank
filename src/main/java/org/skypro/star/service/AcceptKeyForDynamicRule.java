package org.skypro.star.service;

import org.skypro.star.model.DynamicRule;
import org.skypro.star.model.QueryTypeDynamicRule;
import org.skypro.star.repository.TransactionRepository;

import java.util.UUID;

public interface AcceptKeyForDynamicRule {
        boolean checkRule(UUID userId, TransactionRepository transactionRepository, DynamicRule dynamicRule);
}
