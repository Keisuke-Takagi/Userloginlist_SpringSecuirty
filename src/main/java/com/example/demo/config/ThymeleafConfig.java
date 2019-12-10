package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafConfig {

//	Thymeleafで読み込むviewのpathの書き換え
	@Bean
	public SpringResourceTemplateResolver defaultTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("classpath:/views/");
		resolver.setSuffix(".html");
		resolver.setCacheable(false);
		return resolver;
	}
}