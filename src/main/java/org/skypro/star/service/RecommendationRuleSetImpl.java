package org.skypro.star.service;

import org.skypro.star.StarApplication;
import org.skypro.star.controller.RecommendationController;
import org.skypro.star.model.*;
import org.skypro.star.model.mapper.RecommendationMapper;
import org.skypro.star.model.stat.StatUserTriggerRule;
import org.skypro.star.model.stat.StatsUsageGetRecommendationByUser;
import org.skypro.star.repository.RecommendationRepository;
import org.skypro.star.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.skypro.star.service.RecommendationRuleSetImpl.recommendation.getName;

@Service
public class RecommendationRuleSetImpl implements RecommendationRuleSet {
    final DynamicRuleHandler dynamicRuleHandler;
    private final RecommendationRepository recommendationRepository;
    private final TransactionRepository transactionRepository;
    private final RecommendationMapper recommendationMapper;
    private final Logger log = LoggerFactory.getLogger(RecommendationRuleSetImpl.class);

    public RecommendationRuleSetImpl(RecommendationRepository recommendationRepository, TransactionRepository transactionRepository, RecommendationMapper recommendationMapper, DynamicRuleHandler dynamicRuleHandler) {
        this.recommendationRepository = recommendationRepository;
        this.transactionRepository = transactionRepository;
        this.recommendationMapper = recommendationMapper;
        this.dynamicRuleHandler = dynamicRuleHandler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initAfterStartup() {
        rulesData();
    }

    @CachePut(cacheNames = "recordsCache", key = "#recommendationWithDynamicRule")
    public RecommendationAnswerDynamicRule insertData(RecommendationWithDynamicRule recommendationWithDynamicRule) {
        Integer rowNumberId = null;

        DynamicRule[] dynamicRule = recommendationWithDynamicRule.getDynamicRule();
        List<DynamicRule> dynamicRuleList = Arrays.asList(dynamicRule);

        UUID ruleUUID = recommendationWithDynamicRule.getId();
        boolean resultInsertAndUpdateDynamicRule = recommendationRepository.insertRecommendationWithQuery(
                ruleUUID,
                recommendationWithDynamicRule.getName(),
                dynamicRuleList,
                recommendationWithDynamicRule.getText());

        if (resultInsertAndUpdateDynamicRule) {
            rowNumberId = recommendationRepository.getRowNumberId(ruleUUID);
        }
        return new RecommendationAnswerDynamicRule(rowNumberId, recommendationWithDynamicRule);
    }

    /**
     * @param userUUID
     * @return {@linkplain RecommendationAnswerUser}
     * @see <a href=" * @see <a href="https://stackoverflow.com/questions/189559/how-do-i-join-two-lists-in-java">transfer two list to one</a>
     */
    @Override
    public RecommendationAnswerUser getRecommendation(UUID userUUID) {

        List<Recommendation> localRecommendationAnswerUser = handlerOverlap(userUUID);
        log.info("Start incremental recommendation user answer: {}", localRecommendationAnswerUser);

        List<Recommendation> copyOfLocal = new ArrayList<>(localRecommendationAnswerUser);

        List<Recommendation> recommendationAnswerWithDynamicRule = dynamicRuleHandler.handleQueryTypeDynamicRule(userUUID, copyOfLocal);
        log.info("Continue incremental recommendation user answer: {}", recommendationAnswerWithDynamicRule);

        List<Recommendation> result = Stream.concat(localRecommendationAnswerUser.stream(), recommendationAnswerWithDynamicRule.stream()).collect(Collectors.toList());
        log.info("Execute incremental recommendation user answer: {}", result);

        RecommendationAnswerUser recommendationAnswerUser = new RecommendationAnswerUser(userUUID.toString(), result);

        log.info("recommendations = {}", recommendationAnswerUser);
        try {
            recommendationAnswerUser.getRecommendations().stream().map(Recommendation::getId).forEach(recommendationRepository::incrementCountTriggerProcessingUserGetRecommendation);
        } catch (EmptyResultDataAccessException e) {
            log.error("EmptyResultDataAccessException ({}) in this incremental recommendation user answer: {}", e.getMessage(), recommendationAnswerUser);
            throw e;
        }

        return recommendationAnswerUser;
    }

    @CacheEvict(cacheNames = "recordsCache", key = "#ruleId")
    public void deleteData(UUID ruleId) {
        recommendationRepository.deleteRule(ruleId);
    }

    public StatsUsageGetRecommendationByUser getStatsUsageGetRecommendationByUser() {
        return new StatsUsageGetRecommendationByUser(recommendationRepository.getAllIdDynamicRules().stream()
                .map(ruleId -> new StatUserTriggerRule(ruleId, recommendationRepository.getCountTriggerProcessingUserGetRecommendation(ruleId))
                )
                .toArray(StatUserTriggerRule[]::new));
    }

    @Cacheable(cacheNames = "recordsCache")
    public RecommendationsAnswerDynamicRule getData() {

        try {
            List<UUID> allIdDynamicRules = recommendationRepository.getAllIdDynamicRules();
            int lengthArrRules = allIdDynamicRules.size();
            RecommendationAnswerDynamicRule[] rules = new RecommendationAnswerDynamicRule[lengthArrRules];

            for (int i = 0; i < lengthArrRules; i++) {
                UUID ruleId = allIdDynamicRules.get(i);
                System.out.println("Processing ruleId: " + ruleId);
                Recommendation recommendation = recommendationRepository.getRecommendation(ruleId);
                RecommendationWithDynamicRule recommendationWithDynamicRule = recommendationMapper.fromRecommendationToRecommendationWithDynamicRule().apply(recommendation);
                RecommendationAnswerDynamicRule recommendationAnswerDynamicRule = recommendationMapper.fromRecommendationWithDynamicRuleToRecommendationAnswerDynamicRule().apply(recommendationWithDynamicRule);
                rules[i] = recommendationAnswerDynamicRule;
            }

            return new RecommendationsAnswerDynamicRule(rules);
        } catch (SpelEvaluationException e) {
            log.error("SpEL error in getData({}): ", e.getMessage());
            throw e;
        }
    }

    /**
     * Local load of rules and insert to DB for next usage.
     * They are support of usage dynamic rule if repeat already existing structure of REST-request on JSON body,
     * which means the following:
     * 1. Are mean recommendations, which was inserted to DB after start {@link StarApplication#main(String[])  Application}
     * with help {@link RecommendationRuleSetImpl#initAfterStartup() usege @EventListener on initAfterStartup};
     * 2. For update of Recommendation's DynamicRules (for such recommendation which load from {@link RecommendationRuleSetImpl#rulesData() rulesData() (vide supra - in point №1 javaDoc)}
     * on {@link RecommendationRepository PostgreSQL}
     * ) you can describe existing strings
     * on JSON, where execute controller method:
     * {@link  RecommendationController#createDynamicRule(RecommendationWithDynamicRule) RecommendationController.createDynamicRule(exitsting structure of Recommendation, where you can optional add DynamicRule[])},
     * viz. get only {@link Recommendation#getId() ruleId}, {@link Recommendation#getName() ruleName}, {@link Recommendation#getText() TEXT}
     * and put in this method — 'createDynamicRule' with added {@link RecommendationWithDynamicRule#getDynamicRule() DynamicRule[]}
     *
     * @author MK
     * @see RecommendationRuleSetImpl#initAfterStartup() Local load of rules
     * @since 1.0
     */
    public void rulesData() {
        List<String> firstRecommendationRule = Arrays.asList(productType.DEBIT.userUsesMessage(), productType.INVEST.userDoesNotUseMessage(), productType.DEBIT.userSumMessage((short) 1, (short) 1) + " больше 1000 ₽");
        recommendationRepository.insertRecommendationOnPostgresql(UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"), getName(recommendation.invest500), firstRecommendationRule, "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!");

        List<String> secondRecommendationRule = Arrays.asList(productType.DEBIT.userUsesMessage(), productType.DEBIT.userSumMessage((short) 1, (short) 2) + " больше или равна 50 000 ₽ " + "ИЛИ " + productType.SAVING.userSumMessage((short) 1, (short) 2) + " больше или равна 50 000 ₽", productType.DEBIT.userSumMessage((short) 1, (short) 2) + " больше, чем " + productType.DEBIT.userSumMessage((short) 2, (short) 2));
        recommendationRepository.insertRecommendationOnPostgresql(UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"), getName(recommendation.topSaving), secondRecommendationRule, """
                Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!
                
                Преимущества «Копилки»:
                
                Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.
                
                Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.
                
                Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.
                
                Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!
                """);

        String beforeUpCase = productType.DEBIT.userSumMessage((short) 2, (short) 2);
        String afterUpCase = beforeUpCase.substring(0, 1).toUpperCase() + beforeUpCase.substring(1);
        List<String> thirdRecommendationRule = Arrays.asList(productType.CREDIT.userDoesNotUseMessage(), productType.DEBIT.userSumMessage((short) 1, (short) 2) + " больше, чем " + productType.DEBIT.userSumMessage((short) 2, (short) 2), afterUpCase);
        recommendationRepository.insertRecommendationOnPostgresql(UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"), getName(recommendation.justCredit), thirdRecommendationRule, """
                Откройте мир выгодных кредитов с нами!
                
                Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.
                
                Почему выбирают нас:
                
                Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.
                
                Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.
                
                Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.
                
                Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!
                """);
    }

    public List<Recommendation> handlerOverlap(UUID userUUID) {
        return Arrays.stream(recommendation.values()).filter(rule -> rule.checkRule(userUUID, transactionRepository)).map(rec -> recommendationRepository.getRecommendation(getName(rec))).collect(Collectors.toList());
    }

    public enum recommendation implements AcceptKeyForRecommendation {
        invest500 {
            @Override
            public boolean checkRule(UUID userUUID, TransactionRepository repository) {
                return repository.findAptTypeProductByProductType(userUUID, productType.DEBIT.name()) && !repository.findAptTypeProductByProductType(userUUID, productType.INVEST.name()) && repository.findCurrentSumDepositsMoreThatAptSumOrAndEqualsByProductTypeAndAmount(userUUID, productType.SAVING.name(), 1000, false);
            }
        }, topSaving {
            @Override
            public boolean checkRule(UUID userUUID, TransactionRepository repository) {
                return repository.findAptTypeProductByProductType(userUUID, productType.DEBIT.name()) && (repository.findCurrentSumDepositsMoreThatAptSumOrAndEqualsByProductTypeAndAmount(userUUID, productType.DEBIT.name(), 50000, true) | repository.findCurrentSumDepositsMoreThatAptSumOrAndEqualsByProductTypeAndAmount(userUUID, productType.SAVING.name(), 50000, true)) && repository.findSumMoreThatByTransactionTypeAndProductType(userUUID, productType.DEBIT.name());
            }
        }, justCredit {
            @Override
            public boolean checkRule(UUID userUUID, TransactionRepository repository) {
                return !(repository.findAptTypeProductByProductType(userUUID, productType.CREDIT.name())) && repository.findSumMoreThatByTransactionTypeAndProductType(userUUID, productType.DEBIT.name()) && repository.findCurrentSumDepositsMoreThatAptSumOrAndEqualsByProductTypeAndAmount(userUUID, productType.DEBIT.name(), 100000, false);
            }
        };

        public static String getName(recommendation recommendation) {
            return switch (recommendation) {
                case invest500 -> "Invest 500";
                case topSaving -> "Top Saving";
                case justCredit -> "Простой кредит";
            };
        }

    }

    public enum productType {
        DEBIT, CREDIT, SAVING, INVEST;

        public String userUsesMessage() {
            return "Пользователь использует как минимум один продукт с типом " + name();
        }

        public String userDoesNotUseMessage() {
            return "Пользователь не использует продукты с типом " + name();
        }

        public String userSumMessage(short isSumDepositOrWithdraw, short isFewDeposities) {
            String sumDepositOrWithdraw = "";

            switch (isSumDepositOrWithdraw) {
                case 0 -> sumDepositOrWithdraw = "";
                case 1 -> sumDepositOrWithdraw = "Сумма пополнений ";
                case 2 -> sumDepositOrWithdraw = "сумма трат ";
            }

            return switch (isFewDeposities) {
                case 1 -> sumDepositOrWithdraw + "продуктов с типом " + name();
                case 2 -> sumDepositOrWithdraw + "по всем продуктам типа " + name();
                default -> name();
            };
        }
    }
}