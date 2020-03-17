package com.zhan.app.nearby.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@ComponentScan(basePackages = { "com.zhan.app.nearby" })
public class SwaggerConfig {
	@Bean
	public Docket createRestApi() {
		return  new Docket(DocumentationType.SWAGGER_2).host("app.weimobile.com").protocols(Collections.singleton("https")).groupName("nearby_latest").apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.zhan.app.nearby.controller")).paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("漂流瓶 api").termsOfServiceUrl("https://app.weimobile.com/nearby_latest/")
				.description("漂流瓶 api")
				.contact(new Contact("zah", "https://app.weimobile.com/nearby_latest/", "zah5897@qq.com")).version("2.9.8")
				.build();
	}
}