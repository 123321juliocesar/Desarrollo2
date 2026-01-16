package com.epiis.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("API AtelierMode")
						.version("1.0")
						.description("API REST para gestión de usuarios y productos deportivos")
						.contact(new Contact()
								.name("Julio Cesar Gonzales Castillo")
								.email("222175@unamba.edu.pe")));
	}
}