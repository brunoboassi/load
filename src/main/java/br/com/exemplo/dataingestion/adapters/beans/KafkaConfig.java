package br.com.exemplo.dataingestion.adapters.beans;

import br.com.exemplo.dataingestion.adapters.events.entities.LoadEntity;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfig {
    @Bean
    @Scope(value = "prototype")
    public KafkaTemplate<String, LoadEntity> kafkaTemplate1(ProducerFactory<String, LoadEntity> producerFactory){
        Map<String,String> map = new HashMap<>();
        map.put(ProducerConfig.CLIENT_ID_CONFIG,UUID.randomUUID().toString());
        return new KafkaTemplate(producerFactory,false,map);
    }
}
