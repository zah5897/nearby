package com.zhan.app.nearby.dao.base;


import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import com.zhan.app.nearby.dao.base.util.EntityPropertiesReflectUtil;

public class ModelEntityPropertyWrapper {
    private Map<String, String> columnField;
    private Map<String, Type> fieldType;
    private String tableName;
    private String idField;
    private Type idFieldType;
    private GeneratedValue generatedValue;

    public ModelEntityPropertyWrapper() {
    }

    public String praseSQL(Object o) {
        String sql = " insert into " + tableName + " (";
        String filedStr = "";
//        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(columnField.entrySet());

        Map<String, Object> columnsAndValues = EntityPropertiesReflectUtil.prepareSQLValue(o, columnField);
        Set<String> columns = columnsAndValues.keySet();
        for (String column : columns) {
            filedStr += (column + ",");
        }
        filedStr = filedStr.substring(0, filedStr.length() - 1);
        filedStr += " ) ";
        String values = " values ( ";
        for (String column : columns) {
            Object valObj = columnsAndValues.get(column);
            if (valObj instanceof String) {
                values += "'" + valObj.toString() + "',";
            } else {
                values += valObj + ",";
            }
        }
        values = values.substring(0, values.length() - 1);
        values += ")";
        sql += (filedStr + values);
        return sql;
    }

    public boolean isIDAutoIncrement() {
        if (generatedValue != null && generatedValue.strategy() == GenerationType.IDENTITY) {
            return true;
        }
        return false;
    }
//    private Object reflectFiledVal(Object obj, String fieldName) {
//        if (fieldName.equals(idField)) {
//            if (generatedValue == null) {
//                return null;
//            }
//            String generator = generatedValue.generator();
//            if (null != generator && generator.contains("uuid")) {
//                return EntityPropertiesReflectUtil.get32UUID();
//            }
//            if (generatedValue.strategy() == GenerationType.IDENTITY) {
//                return 0;
//            }
//
//            return null;
//        } else {
//            try {
//                return EntityPropertiesReflectUtil.invokeGetMethod(obj, fieldName, null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    public Map<String, String> getColumnField() {
        return columnField;
    }

    public void setColumnField(Map<String, String> columnField) {
        this.columnField = columnField;
    }

    public void setFieldType(Map<String, Type> fieldType) {
        this.fieldType = fieldType;
    }

    public Map<String, Type> getFieldType() {
        return fieldType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Type getIdFieldType() {
        return idFieldType;
    }

    public void setIdFieldType(Type idFieldType) {
        this.idFieldType = idFieldType;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public GeneratedValue getGeneratedValue() {
        return generatedValue;
    }

    public void setGeneratedValue(GeneratedValue generatedValue) {
        this.generatedValue = generatedValue;
    }

    //映射处理java字段和table字段一致的情况
    public void handleResultSet(Object o, ResultSet rs, int rowNumber) {
        Set<String> keySet = columnField.keySet();
        for (String key : keySet) {
            String field = columnField.get(key);
            if (key.equals(field)) {
                continue;
            }
            if (isExistColumn(rs, key)) {
//                if ("create_time".equals(key)) {
//                    int i = 0;
//                    i++;
//                    System.out.println(key);
//                }
                Type fieldClass = fieldType.get(field);
                try {
                	
                	Class<?> clazz=(Class<?>) fieldClass;
                	 
                    if (fieldClass.toString().equals( Date.class.toString())) {
                        EntityPropertiesReflectUtil.invokeSetMethod(o, field, new Object[]{rs.getTimestamp(key)}, (Class<?>)fieldClass);
                    } else {
                        EntityPropertiesReflectUtil.invokeSetMethod(o, field, new Object[]{rs.getObject(key, clazz)}, clazz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //判断结果集中是否包含该字段
    public boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }
}
