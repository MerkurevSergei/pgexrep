/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.merkurevsergei.pgexrep.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Unique table database identifier.
 *
 * @author Merkurev Sergei
 */
@Getter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public final class TableId {

    private final String catalogName;
    private final String schemaName;
    private final String tableName;
    private final String id;

    public TableId(String catalogName, String schemaName, String tableName) {
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.id = catalogName + schemaName + tableName;
    }
}