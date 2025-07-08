package org.skypro.star.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationRepository {
    private final JdbcTemplate jdbcTemplate_h2;
    private final JdbcTemplate jdbcTemplate_posgresql;

    public RecommendationRepository(@Qualifier("recommendationJdbcTemplate") JdbcTemplate jdbcTemplate, JdbcTemplate jdbcTemplate2) {
        this.jdbcTemplate_h2 = jdbcTemplate;
        this.jdbcTemplate_posgresql = jdbcTemplate2;
    }

}
