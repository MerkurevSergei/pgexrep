package com.github.merkurevsergei.pgexrep.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresSchema {

    private final Tables tables = new Tables();
    private final List<TableId> tableIds = new ArrayList<>();

    public void refresh(DatabaseMetaData metadata) {
        try (ResultSet rs = metadata.getTables(null, null, null, new String[]{"VIEW", "MATERIALIZED VIEW", "TABLE"})) {
            try {
                while (rs.next()) {
                    final String catalogName = rs.getString(1);
                    final String schemaName = rs.getString(2);
                    final String tableName = rs.getString(3);
                    final String tableType = rs.getString(4);
                    if (isTableType(tableType)) {
                        TableId tableId = new TableId(catalogName, schemaName, tableName);
                        tableIds.add(tableId);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean isTableType(String tableType) {
        return "TABLE".equals(tableType);
    }
}