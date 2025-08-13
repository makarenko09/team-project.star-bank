package org.skypro.star.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skypro.star.model.DynamicRule;
import org.skypro.star.model.Recommendation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.*;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplatePostgresql;
    private final Logger logger = LoggerFactory.getLogger(RecommendationRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecommendationRepository(@Qualifier("postgresqlJdbcTemplate") JdbcTemplate jdbcTemplatePostgresql) {
        this.jdbcTemplatePostgresql = jdbcTemplatePostgresql;
    }

    //    @Cacheable(value = "recommendationsById", key = "#ruleId()")
    public Recommendation getRecommendation(UUID ruleUUID) {
        logger.info("Fetching recommendation from database for name: {}", ruleUUID);
        Recommendation recommendation = null;

        String searchRuleId = """
                SELECT EXISTS(
                    select 1
                    from recommendation
                    where id = ?
                )
                """;
        Boolean checkRuleName = jdbcTemplatePostgresql.queryForObject(searchRuleId, new Object[]{ruleUUID}, Boolean.class);


        if (checkRuleName) {
            String sql = "select name, id, description from recommendation where id = ?";
            recommendation = jdbcTemplatePostgresql.queryForObject(sql, new Object[]{ruleUUID}, (rs, rowNum) -> new Recommendation(rs.getString("name"), rs.getObject("id", UUID.class), rs.getString("description")));
        } else {
            throw new NoSuchObjectException("on Postgresql" + recommendation.toString());
        }

        return recommendation;
    }

    public List<DynamicRule> getDynamicRulesByIdFromJSONB(UUID ruleId) {
        String sql = "SELECT rules_query FROM recommendation WHERE id = ?";

        return jdbcTemplatePostgresql.queryForObject(
                sql,
                new Object[]{ruleId},
                (rs, rowNum) -> {
                    Array arr = rs.getArray("rules_query");
                    if (arr == null) {
                        return Collections.emptyList();
                    }

                    String[] jsons = (String[]) arr.getArray();
                    if (jsons == null) {
                        return Collections.emptyList();
                    }

                    List<DynamicRule> out = new ArrayList<>(jsons.length);
                    for (String json : jsons) {
                        if (json != null) {
                            try {
                                out.add(objectMapper.readValue(json, DynamicRule.class));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(
                                        "Failed to parse rule JSON: " + json, e
                                );
                            }
                        }
                    }
                    return out;
                }
        );
    }

    //    @Cacheable(value = "recommendationsByName", key = "#ruleName")
    public Recommendation getRecommendation(String ruleName) {
        logger.info("Fetching recommendation from database for name: {}", ruleName);
        Recommendation recommendation = null;

        String searchRuleId = """
                SELECT EXISTS(
                    select 1
                    from recommendation
                    where name = ?
                )
                """;
        Boolean checkRuleName = jdbcTemplatePostgresql.queryForObject(searchRuleId, Boolean.class, ruleName);


        if (checkRuleName) {
            String sql = "select name, id, description from recommendation where name = ?";
            recommendation = jdbcTemplatePostgresql.queryForObject(sql, new Object[]{ruleName}, (rs, rowNum) -> new Recommendation(rs.getString("name"), rs.getObject("id", UUID.class), rs.getString("description")));
        } else {
            throw new NoSuchObjectException("on Postgresql" + recommendation.toString());
        }

        return recommendation;
    }

    public void insertRecommendationOnPostgresql(UUID ruleId, String name, @Nullable List<String> rules, String text) {
        if (!checkRuleIdWithHandlerExc(ruleId)) {
            String sql = "INSERT INTO recommendation (id, name, rules, description, users) VALUES (?, ?, '{}', ? , '{}') ";
            jdbcTemplatePostgresql.update(sql, ruleId, name, text);
            appendRules(ruleId, rules);
        }
    }

    public List<UUID> getAllIdDynamicRules() {
        String sql = "SELECT id FROM recommendation";
        return jdbcTemplatePostgresql.query(sql, (rs, rowNum) -> rs.getObject("id", UUID.class));
    }

    public boolean insertRecommendationWithQuery(UUID ruleId, String name, @Nullable List<DynamicRule> rules, String text) {
        if (checkRuleId(ruleId)) {
            logger.warn("Recommendation with id {} already exists", ruleId);
            return false;
        }


        String sql = "INSERT INTO recommendation (id, name, rules_query, description, users) VALUES (?, ?, '{}', ? , '{}') ";
        int resultUpdate = jdbcTemplatePostgresql.update(sql, ruleId, name, text);
        boolean resultUpdateRules = appendDynamicRules(ruleId, rules);


        boolean success = resultUpdate > 0 && resultUpdateRules;
        logger.info("Inserted recommendation for id: {}, success: {}", ruleId, success);
        return success;
    }

    //    @Cacheable(value = "recommendationsById", key = "#ruleUUID")
    public Integer getRowNumberId(UUID ruleUUID) {
        String searchRowRuleId = """
                WITH incriment AS (
                    SELECT id, ROW_NUMBER() OVER (ORDER BY id ASC) AS sequence_number
                    FROM recommendation
                )
                SELECT sequence_number
                FROM incriment
                WHERE id = ?::uuid
                """;
        try {
            return jdbcTemplatePostgresql.queryForObject(searchRowRuleId, Integer.class, ruleUUID);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private boolean checkRuleId(UUID ruleId) {
        String searchRuleId = """
                SELECT EXISTS(
                    select 1
                    from recommendation
                    where id = ?
                )
                """;
        return jdbcTemplatePostgresql.queryForObject(searchRuleId, Boolean.class, ruleId);
    }

    private Map<Boolean, UUID> getRuleId(UUID ruleId) {
        boolean ruleIdIsPresent = checkRuleId(ruleId);

        UUID id = null;

        if (ruleIdIsPresent) {
            String getRuleId = """
                    select id
                    from recommendation
                    where id = ?
                    """;
            id = jdbcTemplatePostgresql.queryForObject(getRuleId, new Object[]{ruleId}, (rs, rowNum) -> rs.getObject("id", UUID.class));
        }
        HashMap<Boolean, UUID> booleanUUIDHashMap = new HashMap<>();
        booleanUUIDHashMap.put(ruleIdIsPresent, id);
        return booleanUUIDHashMap;
    }

    private boolean checkRuleIdWithHandlerExc(UUID ruleId) {
        Map<Boolean, UUID> resultGetId = getRuleId(ruleId);
        if (Objects.equals(resultGetId.get(true), ruleId)) {
            return true;
        }
        if (Objects.equals(resultGetId.get(false), null)) {
            throw new NoSuchObjectException("on Postgresql" + ruleId.toString());
        }
        return false;
    }

    private boolean appendRules(UUID ruleId, List<String> rules) {
        if (rules == null || rules.isEmpty()) return false;

        String sql = """
                    UPDATE recommendation
                    SET rules = array_append(COALESCE(rules, '{}'), ?)
                    WHERE id = ?
                """;

        int updatedCount = 0;
        for (String rule : rules) {
            updatedCount += jdbcTemplatePostgresql.update(sql, rule, ruleId);
        }
        return updatedCount == rules.size();
    }

    private boolean appendDynamicRules(UUID ruleId, List<DynamicRule> rules) {
        if (rules == null || rules.isEmpty()) {
            logger.warn("Rules list is null or empty for ruleId: {}", ruleId);
            return false;
        }

        List<String> correctQuery = Arrays.asList("USER_OF", "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", "TRANSACTION_SUM_COMPARE");
        boolean containsCorrectQuery = rules.stream().map(DynamicRule::getQuery).allMatch(correctQuery::contains);
        logger.info("Rules list contains correctQuery, boolean = {}", containsCorrectQuery);

        if (!containsCorrectQuery) {
            logger.error("Invalid query in rules: {}", rules);
            throw new NoValidValueException("Invalid query in rules: " + rules);
        }

        if (!checkRuleId(ruleId)) {
            logger.error("No recommendation found for id: {}", ruleId);
            throw new NoValidValueException("No recommendation found for id: " + ruleId);
        }

        String sql = """
                    UPDATE recommendation
                    SET rules_query = array_append(COALESCE(rules_query, '{}'), ?::jsonb)
                    WHERE id = ?
                """;

        int updatedCount = 0;
        for (DynamicRule rule : rules) {
            try {
                String ruleValue = new ObjectMapper().writeValueAsString(rule);
                logger.debug("Appending rule: {} for ruleId: {}", ruleValue, ruleId);
                updatedCount += jdbcTemplatePostgresql.update(sql, ruleValue, ruleId);
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize rule: {} for ruleId: {}", rule, ruleId, e);
                throw new NoValidValueException("Failed to serialize rule: " + e.getMessage());
            }
        }
        boolean success = updatedCount == rules.size();
        logger.info("Appended {} rules for ruleId: {}, success: {}", updatedCount, ruleId, success);
        return success;

    }

    public boolean insertUser(UUID rulesId, UUID userId) {
        String sql = """
                UPDATE recommendation SET users = array_append(users, ?) WHERE id = ?
                """;
        int updates = jdbcTemplatePostgresql.update(sql, userId, rulesId);
        return updates > 0;
    }
}

