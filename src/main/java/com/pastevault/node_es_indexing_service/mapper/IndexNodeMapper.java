package com.pastevault.node_es_indexing_service.mapper;

import com.pastevault.elasticsearch_common.model.IndexVaultNode;
import com.pastevault.kafka_common.model.AvroVaultNode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class IndexNodeMapper {


    public static IndexVaultNode map(AvroVaultNode avroVaultNode) {
        if (avroVaultNode == null) {
            return null;
        }

        return IndexVaultNode.builder()
                .nodeId(avroVaultNode.getNodeId())
                .creatorId(avroVaultNode.getCreatorId())
                .parentPath(avroVaultNode.getParentPath())
                .name(avroVaultNode.getName())
                .createdAt(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(avroVaultNode.getCreatedAt()), ZoneId.systemDefault())
                )
                .updatedAt(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(avroVaultNode.getUpdatedAt()), ZoneId.systemDefault())
                )
                .build();
    }
}
