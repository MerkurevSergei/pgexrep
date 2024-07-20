/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.merkurevsergei.pgexrep.metadata;

import java.util.List;

record Column(String name, int position, int jdbcType, int nativeType, String typeName, String typeExpression,
              String charsetName, int length, Integer scale, boolean optional, boolean autoIncremented,
              boolean generated, String defaultValueExpression, boolean hasDefaultValue, List<String> enumValues,
              String comment) {
}
