package br.com.exemplo.dataingestion.adapters.events.producers;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import br.com.exemplo.dataingestion.adapters.events.entities.LoadEntity;
import br.com.exemplo.dataingestion.domain.producer.ProducerService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Scope(value = "prototype")
public class ProducerServiceImpl implements ProducerService {

    @Value("${data.ingestion.producer.topic}")
    private String producerTopic;

    AtomicInteger records = new AtomicInteger(0);

    @Autowired
    private KafkaTemplate<String, LoadEntity> kafkaTemplate;
    
    @Override
    public void produce(LoadEntity loadEntity) {
        ProducerRecord<String, LoadEntity> producerRecord = new ProducerRecord<String, LoadEntity>(producerTopic, loadEntity);
        kafkaTemplate.send(producerRecord);
        log.info(
            "Records so far: {}", 
            records.incrementAndGet()
        );
    }
}
