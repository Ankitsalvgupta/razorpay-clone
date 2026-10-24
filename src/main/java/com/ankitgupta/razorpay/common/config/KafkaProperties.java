package com.ankitgupta.razorpay.common.config;

import com.ankitgupta.razorpay.common.enums.EventAggregateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
public class KafkaProperties {

    Map<String, String> topics = new HashMap<>();

    public String topicsFor(EventAggregateType aggregateType) {
        String topic = topics.get(aggregateType.name().toLowerCase());
        if (topic == null) {
            throw new IllegalArgumentException("No Kafka topic is configured for aggregate type: " + aggregateType);
        }
        return topic;
    }
}
