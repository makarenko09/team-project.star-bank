package org.skypro.star.repository;

import org.skypro.star.model.Recommendation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplatePostgresql;

    public RecommendationRepository(@Qualifier("postgresqlJdbcTemplate") JdbcTemplate jdbcTemplatePostgresql) {
        this.jdbcTemplatePostgresql = jdbcTemplatePostgresql;
    }

    public Recommendation getRecommendation(UUID ruleId) {
        Recommendation recommendation = null;

        if (checkRuleIdWithHandlerExc(ruleId)) {
            String sql = "select * from recommendation where id = ?";

            Recommendation answer = jdbcTemplatePostgresql.queryForObject(sql, new Object[]{ruleId}, (rs, rowNum) -> new Recommendation(rs.getString("name"), rs.getObject("id", UUID.class), rs.getString("text")));

        }
        return recommendation;
    }

    public Recommendation getRecommendation(String ruleName) {
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

    private boolean checkRuleIdWithHandlerExc(UUID ruleId) {
        Map<Boolean, UUID> resultGetId = getRuleId(ruleId);
        if (Objects.equals(resultGetId.get(true), ruleId)) {
            return true;
        }
        if (Objects.equals(resultGetId.get(false), ruleId)) {
            throw new NoSuchObjectException("on Postgresql" + ruleId.toString());
        }
        return false;
    }

    public void insertRecommendationOnPostgresql(UUID ruleId, String name, @Nullable List<String> rules, String text) {
        if (!checkRuleIdWithHandlerExc(ruleId)) {
            String sql = "INSERT INTO recommendation (id, name, rules, description, users) VALUES (?, ?, '{}', ? , '{}') ";
            jdbcTemplatePostgresql.update(sql, ruleId, name, text);
            appendRules(ruleId, rules);
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

    public boolean insertUser(UUID rulesId, UUID userId) {
        String sql = """
                UPDATE recommendation SET users = array_append(users, ?) WHERE id = ?
                """;
        int updates = jdbcTemplatePostgresql.update(sql, userId, rulesId);
        return updates > 0;
    }
}

