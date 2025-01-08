/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.merkurevsergei.pgexrep.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

record Table(TableId id, List<Column> columnDefs, List<String> pkColumnNames, String defaultCharsetName, String comment,
             List<Attribute> attributes) {

    Table(TableId id) {
        this(id, new ArrayList<>(), new ArrayList<>(), "UTF-8", "", new ArrayList<>());
    }

    public List<String> retrieveColumnNames() {
        return columnDefs.stream()
                .map(Column::name)
                .collect(Collectors.toList());
    }
}