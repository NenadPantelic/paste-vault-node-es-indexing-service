package com.pastevault.node_es_indexing_service.producer;

import com.pastevault.kafka_common.event.KafkaProducer;
import com.pastevault.kafka_common.model.AvroIndexingReport;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class IndexingReportProducer implements KafkaProducer<String, AvroIndexingReport> {

    private final KafkaTemplate<String, AvroIndexingReport> kafkaTemplate;

    public IndexingReportProducer(KafkaTemplate<String, AvroIndexingReport> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, String key, AvroIndexingReport message) {
        log.info("Sending message = '{}' to topic = '{}'", message, topicName);
        // register callback methods for handling events when the response returns
        CompletableFuture<SendResult<String, AvroIndexingReport>> kafkaResultFuture = kafkaTemplate.send(
                topicName, key, message
        );
        addCallback(topicName, message, kafkaResultFuture);
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing Kafka producer!");
            kafkaTemplate.destroy();
        }
    }

    private void addCallback(String topicName,
                             AvroIndexingReport message,
                             CompletableFuture<SendResult<String, AvroIndexingReport>> kafkaResultFuture) {
        kafkaResultFuture.whenComplete((sendResult, throwable) -> {
            if (throwable == null) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                log.debug("Received new metadata. Topic: {}; Partition: {}, Offset: {}, Timestamp: {}, time: {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime());
            } else {
                log.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
            }
        });
    }
}
