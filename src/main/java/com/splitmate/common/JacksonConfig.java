package com.splitmate.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;

@Configuration
public class JacksonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer moneyScale() {
        return builder -> builder.serializerByType(BigDecimal.class, new MoneySerializer());
    }

    static final class MoneySerializer extends JsonSerializer<BigDecimal> {

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeRawValue(Money.normalize(value).toPlainString());
        }
    }
}
