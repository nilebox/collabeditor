package ru.nilebox.collabedit.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author nile
 */
@Configuration
@EnableJpaRepositories("ru.nilebox.collabedit.dao")
@ComponentScan(basePackages = "ru.nilebox.collabedit")
public class AppConfig {
	
}
