/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.github.merkurevsergei.pgexrep.metadata;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Attribute model implementation.
 *
 * @author Merkurev Sergei
 */
record Attribute(String name, String value) {

    public String asString() {
        return value;
    }

    public Integer asInteger() {
        return value == null ? null : Integer.parseInt(value);
    }

    public Long asLong() {
        return value == null ? null : Long.parseLong(value);
    }

    public Boolean asBoolean() {
        return value == null ? null : Boolean.parseBoolean(value);
    }

    public BigInteger asBigInteger() {
        return value == null ? null : new BigInteger(value);
    }

    public BigDecimal asBigDecimal() {
        return value == null ? null : new BigDecimal(value);
    }

    public Float asFloat() {
        return value == null ? null : Float.parseFloat(value);
    }

    public Double asDouble() {
        return value == null ? null : Double.parseDouble(value);
    }
}