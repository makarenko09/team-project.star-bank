package org.skypro.star.configuration;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean(name = "postgresqlDataSource")
    @ConfigurationProperties("spring.datasource.postgresql")
    public DataSource getDataSource(@Value("${spring.datasource.postgresql.url}") String url) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.addDataSourceProperty("portNumber", "5435");
        config.addDataSourceProperty("user", "team-group");
        config.addDataSourceProperty("password", "333");
        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }
    @Primary
    @Bean(name = "postgresqlJdbcTemplate")
    public JdbcTemplate postgresqlJdbcTemplate(@Qualifier("postgresqlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "h2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.h2")
    public DataSource h2DataSource(@Value("${spring.datasource.h2.url}") String url) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setDriverClassName("org.h2.Driver");
        ds.setReadOnly(true);
        return ds;
    }

    @Bean(name = "h2JdbcTemplate")
    public JdbcTemplate h2JdbcTemplate(@Qualifier("h2DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
