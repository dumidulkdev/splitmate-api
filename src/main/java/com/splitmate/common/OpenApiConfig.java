package com.splitmate.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI splitMateApi() {
        return new OpenAPI().info(new Info()
                .title("SplitMate API")
                .version("0.1.0")
                .description("""
                        Split group expenses and see the few payments that settle everyone up.
                        Amounts are currency values with two decimal places. A positive balance means
                        the group owes that member; a negative balance means they owe the group."""));
    }
}
