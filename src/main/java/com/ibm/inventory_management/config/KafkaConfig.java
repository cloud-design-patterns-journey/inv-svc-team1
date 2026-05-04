package com.ibm.inventory_management.config;

import com.ibm.inventory_management.models.AuditEvent;
import com.ibm.inventory_management.models.StockItemCommand;
import com.ibm.inventory_management.models.StockLevelLowEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic commandsTopic() {
        return TopicBuilder.name("stock-items-commands").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic auditTopic() {
        return TopicBuilder.name("stock-items-audit").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic priorityTopic() {
        return TopicBuilder.name("stock-items-priority").partitions(3).replicas(1).build();
    }

    @Bean
    public KafkaTemplate<String, StockItemCommand> commandKafkaTemplate(KafkaProperties props) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfig(props), new StringSerializer(), new JsonSerializer<>()));
    }

    @Bean
    public KafkaTemplate<String, AuditEvent> auditKafkaTemplate(KafkaProperties props) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfig(props), new StringSerializer(), new JsonSerializer<>()));
    }

    @Bean
    public KafkaTemplate<String, StockLevelLowEvent> priorityKafkaTemplate(KafkaProperties props) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfig(props), new StringSerializer(), new JsonSerializer<>()));
    }

    private Map<String, Object> producerConfig(KafkaProperties props) {
        Map<String, Object> config = props.buildProducerProperties(null);
        config.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }
}
