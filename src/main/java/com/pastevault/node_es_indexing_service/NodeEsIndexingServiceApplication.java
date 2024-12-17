package com.pastevault.node_es_indexing_service;

//import com.pastevault.elasticsearch_common.repository.VaultIndexRepository;
import com.pastevault.elasticsearch_common.properties.ElasticsearchConfigProperties;
import com.pastevault.kafka_common.admin.KafkaAdminClient;
import com.pastevault.kafka_common.config.KafkaAdminConfig;
import com.pastevault.kafka_common.config.KafkaConsumerConfig;
import com.pastevault.kafka_common.config.KafkaProducerConfig;
import com.pastevault.kafka_common.properties.KafkaConfigProperties;
import com.pastevault.kafka_common.properties.KafkaConsumerConfigProperties;
import com.pastevault.kafka_common.properties.KafkaProducerConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.retry.support.RetryTemplate;

@ConfigurationPropertiesScan(
        basePackages = {"com.pastevault"},
        basePackageClasses = {
                KafkaConfigProperties.class,
                KafkaProducerConfigProperties.class,
                KafkaConsumerConfigProperties.class,
                ElasticsearchConfigProperties.class
        })
@Import(value = {
        KafkaProducerConfig.class,
        KafkaConsumerConfig.class,
        KafkaAdminConfig.class,
        KafkaAdminClient.class,
        RetryTemplate.class
}
//        VaultIndexRepository.class}
)
@ComponentScan(basePackages = "com.pastevault")
//@EnableElasticsearchRepositories(basePackages = "com.pastevault")

@SpringBootApplication
public class NodeEsIndexingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodeEsIndexingServiceApplication.class, args);
    }

}
