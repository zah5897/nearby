package com.zhan.app.nearby.dao.base;

import javax.persistence.GeneratedValue;

public class ColumnField {
    public String columnName;
    public String fieldName;
    public boolean isID = false;
    public GeneratedValue generatedValue;

    public ColumnField(String columnName, String fieldName) {
        this.columnName = columnName;
        this.fieldName = fieldName;
    }

    public ColumnField(String columnName, String fieldName, GeneratedValue generatedValue) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.generatedValue = generatedValue;
        this.isID = true;
    }
}