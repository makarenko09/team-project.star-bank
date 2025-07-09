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

    public int countTransaction(UUID userUUID) {
        String sql = "SELECT COUNT (*) from transactions where user_ID = ?";
        Integer result = jdbcTemplate_h2.queryForObject(sql, Integer.class, userUUID);
        return result != null ? result : 0;
    }

    public int countTransactionByProductType(UUID userUUID, String productType) {
        String sql = """
                    SELECT COUNT(*)
                    FROM transactions
                    INNER JOIN products ON transactions.product_id = products.id
                    WHERE transactions.user_ID = ? AND products.type = ?
                """;
        Integer result = jdbcTemplate_h2.queryForObject(sql, Integer.class, userUUID, productType);
        return result != null ? result : 0;
    }

    public boolean findAptTypeProductByProductType(UUID userUUID, String productType) {
        String sql = """
                SELECT EXISTS (
                  SELECT 1
                    FROM transactions
                    INNER JOIN products ON transactions.product_id = products.id
                    WHERE transactions.user_ID = ? AND products.type = ?
                    )
                """;
        boolean result = jdbcTemplate_h2.queryForObject(sql, Boolean.class, userUUID, productType);
        return result;
    }

    public boolean findTotalSumDepositsMoreThatAmountByProductTypeAndAmount(UUID userUUID, String productType, Integer amount) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                        FROM transactions t
                        INNER JOIN products p ON t.product_id = p.id
                        WHERE t.user_ID = ? AND p.type = ? and t.type = 'DEPOSIT'
                        HAVING SUM(t.amount) > ?
                        )
                """;
        boolean result = jdbcTemplate_h2.queryForObject(sql, Boolean.class, userUUID, productType, amount);
        return result;
    }

}
