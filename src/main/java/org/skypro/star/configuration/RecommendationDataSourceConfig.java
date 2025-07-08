package org.skypro.star.configuration;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RecommendationDataSourceConfig {

    @Bean(name = "recommendationDataSource")
    public DataSource recommendationDataSource(
            @Value("${application.recommendations-db.url}") String url) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setDriverClassName("org.h2.Driver");
        ds.setReadOnly(true);
        return ds;
    }

    @Bean(name = "recommendationJdbcTemplate")
    public JdbcTemplate recommendationJdbcTemplate(
            DataSource recommendationDataSource) {
        return new JdbcTemplate(recommendationDataSource);
    }
}
