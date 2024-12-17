package com.pastevault.node_es_indexing_service.consumer;

import com.pastevault.elasticsearch_common.model.IndexVaultNode;
import com.pastevault.elasticsearch_common.repository.VaultIndexRepository;
import com.pastevault.kafka_common.admin.KafkaAdminClient;
import com.pastevault.kafka_common.event.KafkaConsumer;
import com.pastevault.kafka_common.model.AvroVaultNode;
import com.pastevault.kafka_common.properties.KafkaConfigProperties;
import com.pastevault.kafka_common.properties.KafkaConsumerConfigProperties;
import com.pastevault.node_es_indexing_service.mapper.IndexNodeMapper;
import com.pastevault.node_es_indexing_service.producer.IndexingReportManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class NodeIndexingConsumer implements KafkaConsumer<String, AvroVaultNode> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaConfigProperties kafkaConfigProperties;
    private final KafkaConsumerConfigProperties kafkaConsumerConfigProperties;
    private final KafkaAdminClient kafkaAdminClient;
//    private final VaultIndexRepository vaultIndexRepository;
    private final IndexingReportManager indexingReportManager;

    public NodeIndexingConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                                KafkaConfigProperties kafkaConfigProperties,
                                KafkaConsumerConfigProperties kafkaConsumerConfigProperties,
                                KafkaAdminClient kafkaAdminClient,
//                                VaultIndexRepository vaultIndexRepository,
                                IndexingReportManager indexingReportManager) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaConfigProperties = kafkaConfigProperties;
        this.kafkaConsumerConfigProperties = kafkaConsumerConfigProperties;
        this.kafkaAdminClient = kafkaAdminClient;
//        this.vaultIndexRepository = vaultIndexRepository;
        this.indexingReportManager = indexingReportManager;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent applicationStartedEvent) {
        kafkaAdminClient.checkIfTopicsCreated();
        log.info("Topics with names {} are ready for operations", kafkaConfigProperties.topicNamesToCreate());
        Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer(
                kafkaConsumerConfigProperties.consumerGroupId())
        ).start();
    }

    @Override
    @KafkaListener(id = "${kafka.consumer.consumer-group-id}", topics = "${kafka.consumer.topic}")
    public void receive(@Payload AvroVaultNode message,
                        @Header(KafkaHeaders.RECEIVED_KEY) String key,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                        @Header(KafkaHeaders.OFFSET) Long offset) {
        log.info("[threadId={}] received a node with key {}, partition {} and offset {}.",
                Thread.currentThread().getId(), key, partition, offset);

        IndexVaultNode indexVaultNode = IndexNodeMapper.map(message);


        boolean indexed;
        String error = null;
        if (indexVaultNode != null) {
            String nodeId = indexVaultNode.getNodeId();
            log.info("Indexing node[id = {}] to Elasticsearch.", nodeId);
            try {
//                vaultIndexRepository.save(indexVaultNode);
                indexed = true;
                log.info("Node[id = {}] has been successfully stored.", nodeId);
            } catch (Exception e) {
                log.error("Unable to store the index node {} due to {}", indexVaultNode, e.getMessage(), e);
                indexed = false;
                error = e.getMessage();
            }

            indexingReportManager.report(indexVaultNode, indexed, error);
        }
    }
}