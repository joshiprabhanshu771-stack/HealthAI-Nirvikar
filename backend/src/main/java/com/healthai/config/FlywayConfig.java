package com.healthai.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Explicit Flyway configuration bean.
 * Ensures Flyway migrations run automatically on Spring Boot startup across all environments.
 */
@Configuration
public class FlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

    @Value("${spring.flyway.locations:classpath:db/migration}")
    private String migrationLocations;

    @Value("${spring.flyway.baseline-on-migrate:false}")
    private boolean baselineOnMigrate;

    @Value("${spring.flyway.enabled:true}")
    private boolean flywayEnabled;

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        if (!flywayEnabled) {
            logger.info("Flyway migration is disabled via spring.flyway.enabled=false");
            return null;
        }

        logger.info("Initializing Flyway database migration with location: {}", migrationLocations);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(migrationLocations.split(","))
                .baselineOnMigrate(baselineOnMigrate)
                .load();

        return flyway;
    }
}
