package com.omo.config.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;

@Configuration
class JacksonConfig {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.findAndAddModules();

            builder.changeDefaultPropertyInclusion(
                v -> v.withValueInclusion(JsonInclude.Include.NON_NULL)
            );

            builder.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

            builder.enable(
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES
            );
            builder.disable(
                DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
            );
        };
    }
}
