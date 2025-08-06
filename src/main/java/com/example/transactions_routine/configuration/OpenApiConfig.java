package com.example.transactions_routine.configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transactions Routine API")
                        .description("""
                                An API for managing financial accounts and transactions, supporting various types like 
                                purchases, withdrawals, installments, and credit vouchers, with automatic processing 
                                of monetary amounts based on operation type.
                                """.trim())
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Reginaldo Ribeiro")
                                .email("reginaldolribeiro@gmail.com")
                                .url("https://github.com/reginaldolribeiro/transactions-routine/")))
                .externalDocs(new ExternalDocumentation()
                        .description("Find more info about this API")
                        .url("https://github.com/reginaldolribeirotransactions-routine/README.md"));
    }

}
