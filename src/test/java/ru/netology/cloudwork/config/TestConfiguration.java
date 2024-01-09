package ru.netology.cloudwork.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

@Configuration
public class TestConfiguration {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public MySQLContainer<?> mySQLContainer() {
        return new MySQLContainer<>("mysql");
    }

    @Bean
    public DataSource daDataSource(MySQLContainer<?> mySQLContainer) {
        var hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(mySQLContainer.getJdbcUrl());
        hikariDataSource.setUsername(mySQLContainer.getUsername());
        hikariDataSource.setPassword(mySQLContainer.getPassword());
        return hikariDataSource;
    }
}
