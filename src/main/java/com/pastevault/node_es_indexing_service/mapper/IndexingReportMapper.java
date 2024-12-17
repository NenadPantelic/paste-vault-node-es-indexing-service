package com.pastevault.node_es_indexing_service.mapper;

import com.pastevault.elasticsearch_common.model.IndexVaultNode;
import com.pastevault.kafka_common.model.AvroIndexingReport;

public class IndexingReportMapper {


    public static AvroIndexingReport map(IndexVaultNode indexVaultNode,
                                         boolean indexed,
                                         String error) {
        if (indexVaultNode == null) {
            return null;
        }

        return AvroIndexingReport.newBuilder()
                .setNodeId(indexVaultNode.getNodeId())
                .setIndexed(indexed)
                .setError(error)
                .build();
    }
}
