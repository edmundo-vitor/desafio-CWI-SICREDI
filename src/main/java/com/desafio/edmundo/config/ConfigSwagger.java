package com.desafio.edmundo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class ConfigSwagger {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
          .select()
          .apis(RequestHandlerSelectors.basePackage("com.desafio.edmundo.controller"))
          .paths(PathSelectors.any())
          .build()
          .ignoredParameterTypes()
          .useDefaultResponseMessages(false)
          .apiInfo(apiInfo());
    }
    
    @Bean
    public UiConfiguration UiConfig() {
    	return UiConfigurationBuilder.builder()
    			.deepLinking(true)
    			.docExpansion(DocExpansion.NONE)
    			.defaultModelsExpandDepth(-1)
    			.defaultModelRendering(ModelRendering.EXAMPLE)
    			.operationsSorter(OperationsSorter.METHOD)
    			.build();
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API de votação")
                .description("API do teste técnico CWI - SICREDI")
                .build();
    }
}
