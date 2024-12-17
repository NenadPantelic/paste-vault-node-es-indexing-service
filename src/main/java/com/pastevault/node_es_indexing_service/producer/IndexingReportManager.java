package com.pastevault.node_es_indexing_service.producer;

import com.pastevault.elasticsearch_common.model.IndexVaultNode;
import com.pastevault.kafka_common.event.KafkaProducer;
import com.pastevault.kafka_common.model.AvroIndexingReport;
import com.pastevault.kafka_common.properties.KafkaProducerConfigProperties;
import com.pastevault.node_es_indexing_service.mapper.IndexingReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IndexingReportManager {

    private final KafkaProducerConfigProperties kafkaProducerConfigProperties;
    private final KafkaProducer<String, AvroIndexingReport> kafkaProducer;

    public IndexingReportManager(KafkaProducerConfigProperties kafkaProducerConfigProperties,
                                 KafkaProducer<String, AvroIndexingReport> kafkaProducer) {
        this.kafkaProducerConfigProperties = kafkaProducerConfigProperties;
        this.kafkaProducer = kafkaProducer;
    }

    public void report(IndexVaultNode node, boolean indexed, String error) {
        log.info("Getting ready to report {}. Sending to Kafka topic {}", node.getNodeId(), kafkaProducerConfigProperties.topic());
        AvroIndexingReport indexingReport = IndexingReportMapper.map(node, indexed, error);
        kafkaProducer.send(kafkaProducerConfigProperties.topic(), node.getCreatorId(), indexingReport);
    }
}
