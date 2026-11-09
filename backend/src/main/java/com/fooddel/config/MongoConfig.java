package com.fooddel.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Stops Spring Data from adding a "_class" field to every document it writes,
 * so documents stay small and readable.
 */
@Configuration
public class MongoConfig implements InitializingBean {

    private final MappingMongoConverter converter;

    public MongoConfig(MappingMongoConverter converter) {
        this.converter = converter;
    }

    @Override
    public void afterPropertiesSet() {
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }
}
