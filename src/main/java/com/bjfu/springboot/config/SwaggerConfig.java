package com.bjfu.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class SwaggerConfig {

    @Bean
    public Docket restApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Standard API")  // Group name for the API
                .apiInfo(apiInfo("Building RESTful APIs with Swagger2 in Spring Boot", "1.0"))  // API info title and version
                .useDefaultResponseMessages(true)  // Use default response messages
                .forCodeGeneration(false)  // Disable code generation
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dongsheng.springboot.controller"))  // Select controllers from the specified base package
                .paths(PathSelectors.any())  // Include all paths
                .build();
    }

    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder()
                .title(title)  // Set the title of the API
                .description("dongsheng")  // API description
                .termsOfServiceUrl("dongsheng")  // Terms of service URL
                .contact(new Contact("xqnode", "dongsheng", "dongsheng@163.com"))  // Contact details
                .version(version)  // Set API version
                .build();
    }
}
