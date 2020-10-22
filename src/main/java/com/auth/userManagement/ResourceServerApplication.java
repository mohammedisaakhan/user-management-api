package com.auth.userManagement;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
public class ResourceServerApplication {

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource mainDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();}

	public static void main(String[] args) {
		SpringApplication.run(ResourceServerApplication.class, args);
	}
}
