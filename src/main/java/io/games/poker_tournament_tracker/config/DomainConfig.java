package io.games.poker_tournament_tracker.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan("io.games.poker_tournament_tracker.domain")
@EnableJpaRepositories("io.games.poker_tournament_tracker.repos")
@EnableTransactionManagement
public class DomainConfig {}
