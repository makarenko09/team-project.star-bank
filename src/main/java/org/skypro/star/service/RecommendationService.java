package org.skypro.star.service;

import org.skypro.star.model.Recommendation;
import org.skypro.star.model.RecommendationAnswerUser;
import org.skypro.star.repository.RecommendationRepository;
import org.skypro.star.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final TransactionRepository transactionRepository;
    private final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 TransactionRepository transactionRepository) {
        this.recommendationRepository = recommendationRepository;
        this.transactionRepository = transactionRepository;
        logger.info("RecommendationService initialized with TransactionRepository");
    }

    @Cacheable(value = "recommendationsById", key = "#userId")
    public RecommendationAnswerUser getRecommendation(UUID userId) {
        logger.info("Getting real recommendations for user: {}", userId);

        List<Recommendation> recommendations = getRealRecommendationsForUser(userId);
        return new RecommendationAnswerUser(userId.toString(), recommendations);
    }

    public RecommendationAnswerUser getRecommendationsByUsername(String username) {
        logger.info("Getting recommendations for username: {}", username);
        UUID userId = convertUsernameToUserId(username);
        return getRecommendation(userId);
    }

    private List<Recommendation> getRealRecommendationsForUser(UUID userId) {
        logger.info("Analyzing transactions and generating recommendations for user: {}", userId);
        List<Recommendation> recommendations = new ArrayList<>();

        try {
            // 1. Анализ количества транзакций
            int transactionCount = transactionRepository.countTransaction(userId);
            logger.info("User {} has {} total transactions", userId, transactionCount);

            // 2. Анализ по типам продуктов
            analyzeByProductType(userId, recommendations);

            // 3. Анализ сумм депозитов
            analyzeDepositSums(userId, recommendations);

            // 4. Сравнение депозитов и снятий
            analyzeTransactionBalance(userId, recommendations);

            // 5. Проверка наличия определенных продуктов
            checkProductAvailability(userId, recommendations);

        } catch (Exception e) {
            logger.error("Error generating recommendations for user: {}", userId, e);
            // Добавляем fallback рекомендацию в случае ошибки
            recommendations.add(new Recommendation(
                    "Техническая рекомендация",
                    UUID.fromString("00000000-0000-0000-0000-000000000001"),
                    "Приносим извинения, в данный момент система рекомендаций временно недоступна. Попробуйте позже."
            ));
        }

        logger.info("Generated {} recommendations for user: {}", recommendations.size(), userId);
        return recommendations;
    }

    private void analyzeByProductType(UUID userId, List<Recommendation> recommendations) {
        try {
            // Анализ кредитных продуктов
            int creditTransactions = transactionRepository.countTransactionByProductType(userId, "CREDIT");
            boolean hasCreditProducts = transactionRepository.findAptTypeProductByProductType(userId, "CREDIT");

            if (hasCreditProducts && creditTransactions > 5) {
                recommendations.add(new Recommendation(
                        "Кредитная карта с кэшбэком",
                        UUID.fromString("00000000-0000-0000-0000-000000000002"),
                        "На основе вашей активности с кредитными продуктами, рекомендуем карту с повышенным кэшбэком до 5%"
                ));
            }

            // Анализ депозитных продуктов
            int depositTransactions = transactionRepository.countTransactionByProductType(userId, "DEPOSIT");
            boolean hasDepositProducts = transactionRepository.findAptTypeProductByProductType(userId, "DEPOSIT");

            if (hasDepositProducts && depositTransactions > 3) {
                recommendations.add(new Recommendation(
                        "Накопительный счет",
                        UUID.fromString("00000000-0000-0000-0000-000000000003"),
                        "Учитывая вашу активность по депозитам, предлагаем открыть накопительный счет с повышенной ставкой"
                ));
            }

        } catch (Exception e) {
            logger.warn("Error in product type analysis for user: {}", userId, e);
        }
    }

    private void analyzeDepositSums(UUID userId, List<Recommendation> recommendations) {
        try {
            // Проверка больших сумм депозитов
            boolean hasLargeDeposits = transactionRepository.findCurrentSumDepositsMoreThatAptSumOrAndEqualsByProductTypeAndAmount(
                    userId, "DEPOSIT", 50000, true);

            if (hasLargeDeposits) {
                recommendations.add(new Recommendation(
                        "Премиальная дебетовая карта",
                        UUID.fromString("00000000-0000-0000-0000-000000000004"),
                        "Для крупных сумм рекомендуем премиальную карту с дополнительными привилегиями и страховкой"
                ));
            }

            // Сравнение с конкретной суммой
            boolean compareDeposit = transactionRepository.compareCurrentSumDepositsToAptSumByProductTypeAndAmountAndAnyOperator(
                    userId, "DEPOSIT", 100000, ">");

            if (compareDeposit) {
                recommendations.add(new Recommendation(
                        "Инвестиционный портфель",
                        UUID.fromString("00000000-0000-0000-0000-000000000005"),
                        "Рассмотрите возможность инвестирования части средств для получения дополнительного дохода"
                ));
            }

        } catch (Exception e) {
            logger.warn("Error in deposit sum analysis for user: {}", userId, e);
        }
    }

    private void analyzeTransactionBalance(UUID userId, List<Recommendation> recommendations) {
        try {
            // Анализ баланса по кредитным продуктам
            boolean creditBalancePositive = transactionRepository.findSumMoreThatByTransactionTypeAndProductType(userId, "CREDIT");

            if (!creditBalancePositive) {
                recommendations.add(new Recommendation(
                        "Рефинансирование кредита",
                        UUID.fromString("00000000-0000-0000-0000-000000000006"),
                        "Рассмотрите возможность рефинансирования ваших кредитов под более низкий процент"
                ));
            }

            // Анализ баланса по депозитным продуктам
            boolean depositBalancePositive = transactionRepository.findSumMoreThatByTransactionTypeAndProductType(userId, "DEPOSIT");

            if (depositBalancePositive) {
                recommendations.add(new Recommendation(
                        "Сберегательная программа",
                        UUID.fromString("00000000-0000-0000-0000-000000000007"),
                        "Отличные сбережения! Предлагаем программу с повышенной процентной ставкой"
                ));
            }

        } catch (Exception e) {
            logger.warn("Error in transaction balance analysis for user: {}", userId, e);
        }
    }

    private void checkProductAvailability(UUID userId, List<Recommendation> recommendations) {
        try {
            // Проверка отсутствия кредитных продуктов
            boolean hasCredit = transactionRepository.findAptTypeProductByProductType(userId, "CREDIT");
            if (!hasCredit) {
                recommendations.add(new Recommendation(
                        "Первая кредитная карта",
                        UUID.fromString("00000000-0000-0000-0000-000000000008"),
                        "Рекомендуем оформить первую кредитную карту с льготным периодом и низкой ставкой"
                ));
            }

            // Проверка отсутствия депозитных продуктов
            boolean hasDeposit = transactionRepository.findAptTypeProductByProductType(userId, "DEPOSIT");
            if (!hasDeposit) {
                recommendations.add(new Recommendation(
                        "Накопительный счет",
                        UUID.fromString("00000000-0000-0000-0000-000000000009"),
                        "Начните накапливать средства на накопительном счете с ежедневной капитализацией"
                ));
            }

            // Проверка отсутствия сберегательных продуктов
            boolean hasSavings = transactionRepository.findAptTypeProductByProductType(userId, "SAVINGS");
            if (!hasSavings) {
                recommendations.add(new Recommendation(
                        "Сберегательный вклад",
                        UUID.fromString("00000000-0000-0000-0000-000000000010"),
                        "Рассмотрите открытие сберегательного вклада для долгосрочного накопления"
                ));
            }

        } catch (Exception e) {
            logger.warn("Error in product availability check for user: {}", userId, e);
        }
    }

    private UUID convertUsernameToUserId(String username) {
        // Простая конвертация имени пользователя в UUID
        // В реальной системе здесь бы было обращение к базе данных
        return UUID.nameUUIDFromBytes(username.getBytes());
    }

    // Вспомогательный метод для логирования рекомендаций
    private void logRecommendation(Recommendation recommendation, UUID userId) {
        logger.info("Recommended for user {}: {} - {}", userId, recommendation.getName(), recommendation.getText());
    }
}