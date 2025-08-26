package org.skypro.star.model;

import org.skypro.star.repository.TransactionRepository;
import org.skypro.star.service.AcceptKeyForDynamicRule;
import org.skypro.star.service.RecommendationRuleSetImpl;

import java.util.UUID;

public enum QueryTypeDynamicRule implements AcceptKeyForDynamicRule {


    USER_OF {
        @Override
        public boolean checkRule(UUID userId, TransactionRepository transactionRepository, DynamicRule dynamicRule) {
            return transactionRepository.findAptTypeProductByProductType(userId, RecommendationRuleSetImpl.productType.valueOf(dynamicRule.getArguments().get(0)).name());
        }

    }, TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW {
        @Override
        public boolean checkRule(UUID userId, TransactionRepository transactionRepository, DynamicRule dynamicRule) {
            String nameProductType = RecommendationRuleSetImpl.productType.valueOf(dynamicRule.getArguments().get(0)).name();
            return transactionRepository.compareCurrentSumDepositsToAptSumByProductTypeAndAmountAndAnyOperator(userId, nameProductType, null, dynamicRule.getArguments().get(1));
        }
    }, TRANSACTION_SUM_COMPARE {
        @Override
        public boolean checkRule(UUID userId, TransactionRepository transactionRepository, DynamicRule dynamicRule) {
            String nameProductType = RecommendationRuleSetImpl.productType.valueOf(dynamicRule.getArguments().get(0)).name();
            String transactionType = TransactionRepository.transactionType.valueOf(dynamicRule.getArguments().get(1)).name();
            String operator = dynamicRule.getArguments().get(2);
            Integer amountTransaction = Integer.valueOf(dynamicRule.getArguments().get(3));

//            int sumOfTransactions = transactionRepository.getSumOfTransactionsByProductTypeAndTransactionType(userId, nameProductType, transactionType);
//            return transactionRepository.compareCurrentSumDepositsToAptSumByProductTypeAndAmountAndAnyOperator(userId, nameProductType, amountTransaction, dynamicRule.getArguments().get(2));
            return transactionRepository.compareCurrentSumDepositsToAptSumByProductTypeAndAmountAndAnyOperatorAndTransactionType(userId, nameProductType, transactionType, operator, amountTransaction);
        }
    };
}
