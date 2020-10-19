package com.auth.userManagement.configuration;

import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableWebSecurity
@EnableTransactionManagement
@ComponentScan(basePackages = "com.auth")
public class Config {

	// logger
	private Logger logger = Logger.getLogger(getClass().getName());

	@Bean(name = "mailSender")
	public MailSender javaMailService() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost("smtp.gmail.com");
		javaMailSender.setPort(587);
		javaMailSender.setProtocol("smtp");
		javaMailSender.setUsername("mohammed.isaakhan@gmail.com");
		javaMailSender.setPassword("frwbsckhgwszvjnr");
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.auth", "true");
		mailProperties.put("mail.smtp.starttls.enable", "true");
		mailProperties.put("mail.smtp.debug", "true");
		javaMailSender.setJavaMailProperties(mailProperties);
		return javaMailSender;
	}
	
	@Bean
     public MessageSource messageSource() {
     final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
     messageSource.setBasename("classpath:messages");
     messageSource.setUseCodeAsDefaultMessage(true);
     messageSource.setDefaultEncoding("UTF-8");
     messageSource.setCacheSeconds(0);
     return messageSource;
     }
}
