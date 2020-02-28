package com.zhan.app.nearby.dao.base.util;

import org.springframework.util.StringUtils;

import com.zhan.app.nearby.dao.base.ModelEntityPropertyWrapper;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author cuisuqiang
 * @version 1.0
 * @说明 对象操纵高级方法
 * @since
 */
public class EntityPropertiesReflectUtil {
    static SimpleDateFormat sqlDateTimeSpf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ModelEntityPropertyWrapper createModelEntityPropertyWrapper(Class<?> clazz) {
        ModelEntityPropertyWrapper wrapper = new ModelEntityPropertyWrapper();
        praseColumnFiled(clazz, wrapper);
        wrapper.setTableName(getTableName(clazz));
        return wrapper;
    }


    public static String getTableName(Class<?> clazz) {
        String tableName = null;
        Table table = (Table) clazz.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.name();
        }
        if (StringUtils.isEmpty(tableName)) {
            tableName = clazz.getSimpleName();
        }
        return tableName;
    }

    private static Object getFieldValue(Object object, String fieldName) throws Exception {
        return invokeGetMethod(object, fieldName, null);
    }

    /**
     * 获得对象属性的值
     */
    public static Object invokeGetMethod(Object object, String methodName, Object[] args) throws Exception {
        Class<? extends Object> ownerClass = object.getClass();
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        Method method = null;
        try {
            method = ownerClass.getMethod("get" + methodName);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
            return " can't find 'get" + methodName + "' method";
        }
        return method.invoke(object);
    }

    public static void praseColumnFiled(Class<?> clazz, ModelEntityPropertyWrapper wrapper) {
        // 获得对象属性
        List<Field> allFields = new ArrayList<Field>();
        //获取全部字段
        getClassAllField(clazz, allFields);
        Map<String, String> columnFields = new TreeMap<>();
        Map<String, Class<?>> fieldTypes = new HashMap<>();
        for (Field f : allFields) {
            f.setAccessible(true);
            // 忽略的字段
            Transient ignore = f.getAnnotation(Transient.class);
            if (ignore != null) {
                continue;
            }
            String filedName = f.getName();
            String columnName = filedName; //默认是相同的名字
            Class<?> fieldType = (Class<?>) f.getGenericType();
            Column cAnno = f.getAnnotation(Column.class);
            if (cAnno != null) {
                columnName = cAnno.name();
                if (StringUtils.isEmpty(columnName)) {
                    columnName = f.getName();
                }
            }
            Id id = f.getAnnotation(Id.class);
            if (id != null) { //主键
                wrapper.setIdField(filedName);
                GeneratedValue gv = f.getAnnotation(GeneratedValue.class);
                wrapper.setGeneratedValue(gv);
                if (gv != null && gv.strategy() == GenerationType.IDENTITY) {
                    wrapper.setIdFieldType(fieldType);
                    continue;
                }
            }
            columnFields.put(columnName, filedName);
            fieldTypes.put(filedName, fieldType);
        }
        wrapper.setColumnField(columnFields);
        wrapper.setFieldType(fieldTypes);
    }

    /**
     * 获取全部字段，包含父类的字段
     *
     * @param clazz
     * @param allFields
     * @return
     */
    private static List<Field> getClassAllField(Class<? extends Object> clazz, List<Field> allFields) {
        if (clazz.getName().equals(Object.class.getName())) {
            return allFields;
        }
        Field fields[] = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (!allFields.contains(f)) {
                allFields.add(f);
            }
        }
        return getClassAllField(clazz.getSuperclass(), allFields);
    }

    public static String get32UUID() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    public static void invokeSetMethod(Object owner, String methodName, Object[] args, Class<?> paramClass)
            throws Exception {
        Class<? extends Object> ownerClass = owner.getClass();
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        Method method = null;
        method = ownerClass.getMethod("set" + methodName, paramClass);
        method.invoke(owner, args);
    }

    public static Map<String, Object> prepareSQLValue(Object o, Map<String, String> columnField) {
        Map<String, Object> sqlValues = new HashMap<>();
        Set<String> columns = columnField.keySet();
        for (String column : columns) {
            Object fieldValue = null;
            try {
                fieldValue = getFieldValue(o, columnField.get(column));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fieldValue != null) {
                sqlValues.put(column, parse(fieldValue));
            }
        }
        return sqlValues;
    }

    public static Object parse(Object value) {
        if (value instanceof Date) {
            return sqlDateTimeSpf.format(value);
        }
        return value.toString();
    }
}
