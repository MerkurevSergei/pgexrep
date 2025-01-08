/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.merkurevsergei.pgexrep.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Set of PostgreSQL tables.
 *
 * @author Merkurev
 */
public final class Tables {

    private final Map<TableId, Table> tablesById;


    /**
     * Create an empty set of definitions.
     */
    public Tables() {
        this.tablesById = new HashMap<>();
    }

    public void clear() {
        tablesById.clear();
    }

    /**
     * Get the number of tables that are in this object.
     *
     * @return the table count
     */
    public int size() {
        return tablesById.size();
    }

    /**
     * Add or update table definition.
     *
     * @param tableId               table identifier.
     * @param columnDefs            the list of column definitions; may not be null or empty.
     * @param primaryKeyColumnNames the list of the column names that make up the primary key; may be null or empty.
     * @param defaultCharsetName    the name of the character set that should be used by default.
     * @param attributes            the list of attribute definitions; may not be null or empty.
     * @return table definition.
     */
    public Table addTable(TableId tableId, List<Column> columnDefs, List<String> primaryKeyColumnNames,
                          String defaultCharsetName, List<Attribute> attributes) {
        Table newTable = new Table(tableId, columnDefs, primaryKeyColumnNames, defaultCharsetName, "", attributes);
        return tablesById.put(tableId, newTable);
    }

    public Table addTable(TableId tableId) {
        Table newTable = new Table(tableId);
        return tablesById.put(tableId, newTable);
    }

    public Collection<Table> getTables() {
        return tablesById.values();
    }

    /**
     * Get table definition by identifier.
     *
     * @param tableId the identifier of the table.
     * @return the table definition, or null if there was no definition for the identified table.
     */
    public Table getTable(TableId tableId) {
        return tablesById.get(tableId);
    }

    /**
     * Get table definition by catalog, schema and table name.
     *
     * @param catalogName database catalog name.
     * @param schemaName  database schema name.
     * @param tableName   table name.
     * @return the table definition, or null if there was no definition for the identified table
     */
    public Table getTable(String catalogName, String schemaName, String tableName) {
        return getTable(new TableId(catalogName, schemaName, tableName));
    }
}
