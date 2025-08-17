package org.skypro.star.repository;

import org.skypro.star.exception.NoValidValueException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplateH2;

    public TransactionRepository(@Qualifier("h2JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplateH2 = jdbcTemplate;
    }

    public int countTransaction(UUID userUUID) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_ID = ?";
        Integer result = jdbcTemplateH2.queryForObject(sql, Integer.class, userUUID);
        return result != null ? result : 0;
    }

    public int countTransactionByProductType(UUID userUUID, String productType) {
        String sql = """
                SELECT COUNT(*)
                FROM transactions
                INNER JOIN products ON transactions.product_id = products.id
                WHERE transactions.user_ID = ? AND products.type = ?
                """;
        Integer result = jdbcTemplateH2.queryForObject(sql, Integer.class, userUUID, productType);
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
        Boolean result = jdbcTemplateH2.queryForObject(sql, Boolean.class, userUUID, productType);
        return result != null && result;
    }

    public boolean findCurrentSumDepositsMoreThatAptSumOrAndEqualsByProductTypeAndAmount(
            UUID userUUID, String productType, Integer amount, Boolean equals) {
        String sql = """
                SELECT SUM(t.AMOUNT)
                FROM transactions t
                INNER JOIN products p ON t.product_id = p.id
                WHERE t.user_ID = ? AND p.type = ? AND t.type = 'DEPOSIT'
                """;
        Integer sum = jdbcTemplateH2.queryForObject(sql, Integer.class, userUUID, productType);
        sum = sum != null ? sum : 0;
        return equals ? sum >= amount : sum > amount;
    }

    public boolean compareCurrentSumDepositsToAptSumByProductTypeAndAmountAndAnyOperator(
            UUID userUUID, String productType, Integer amount, String operator) {
        List<String> correctOperator = Arrays.asList(">", "<", "=", ">=", "<=");
        if (!correctOperator.contains(operator)) {
            throw new NoValidValueException(operator);
        }

        String sql = """
                SELECT SUM(t.AMOUNT)
                FROM transactions t
                INNER JOIN products p ON t.product_id = p.id
                WHERE t.user_ID = ? AND p.type = ? AND t.type = 'DEPOSIT'
                """;
        Integer sum = jdbcTemplateH2.queryForObject(sql, Integer.class, userUUID, productType);
        sum = sum != null ? sum : 0;

        return switch (operator) {
            case ">" -> sum > amount;
            case "<" -> sum < amount;
            case "=" -> sum.equals(amount);
            case ">=" -> sum >= amount;
            case "<=" -> sum <= amount;
            default -> throw new NoValidValueException(operator);
        };
    }

    public boolean findSumMoreThatByTransactionTypeAndProductType(UUID userUUID, String productType) {
        String sql = """
                SELECT
                    SUM(CASE WHEN t.type = 'DEPOSIT' THEN t.amount ELSE 0 END) >
                    SUM(CASE WHEN t.type = 'WITHDRAWAL' THEN t.amount ELSE 0 END)
                FROM transactions t
                INNER JOIN products p ON t.product_id = p.id
                WHERE t.user_ID = ? AND p.type = ?
                """;
        Boolean result = jdbcTemplateH2.queryForObject(sql, Boolean.class, userUUID, productType);
        return result != null && result;
    }

    public String[] findFullNameByUsername(String username) {
        Boolean checkRuleName = checkUserId(username);
        String[] fullName = new String[2];

        if (checkRuleName) {
            String sql = """
                    select FIRST_NAME,LAST_NAME 
                    from USERS
                    where USERNAME = ?
                    """;
            fullName = jdbcTemplateH2.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                return new String[]{firstName, lastName};
            });
        }
        return fullName;
    }

    private Boolean checkUserId(String username) {
        String searchRuleId = """
                SELECT EXISTS(
                    select id
                    from USERS
                    where USERNAME = ?
                )
                """;
        return jdbcTemplateH2.queryForObject(searchRuleId, new Object[]{username}, Boolean.class);
    }

    public UUID findUserUUIDByUsername(String username) {
        Boolean checkRuleName = checkUserId(username);

        UUID id = null;
        if (checkRuleName) {

                String getUserId = """
                    select id
                    from USERS
                    where USERNAME = ?
                    """;
                id = jdbcTemplateH2.queryForObject(getUserId, new Object[]{username}, (rs, rowNum) -> rs.getObject("id", UUID.class));

        }
        return id;
    }
}