package br.com.exemplo.dataingestion.adapters.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import br.com.exemplo.dataingestion.adapters.events.entities.LoadEntity;

@Configuration
public class KafkaConfig {
    @Bean
    @Scope(value = "prototype")
    public KafkaTemplate<String, LoadEntity> kafkaTemplate1(ProducerFactory<String, LoadEntity> producerFactory){
        // Map<String,String> map = new HashMap<>();
        // map.put(ProducerConfig.CLIENT_ID_CONFIG,UUID.randomUUID().toString());
        KafkaTemplate<String, LoadEntity> template = new KafkaTemplate<String, LoadEntity> (producerFactory,false);
        return template;
    }
}
