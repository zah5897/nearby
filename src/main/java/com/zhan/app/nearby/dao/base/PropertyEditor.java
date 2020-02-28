package com.zhan.app.nearby.dao.base;

import org.springframework.beans.BeanWrapper;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyEditor extends PropertyEditorSupport {

    private Class<?> clazz;
    private String name;

    public PropertyEditor(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }


    public void regist(BeanWrapper beanWrapper) {
        beanWrapper.registerCustomEditor(clazz, name, this);
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        Method method = null;
        try {
            method = clazz.getMethod("valueOf", String.class);
            setValue(method.invoke(null, text));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setValue(final Object value) {
        Method method = null;
        try {
            method = clazz.getMethod("valueOf", String.class);
            super.setValue(method.invoke(null, value.toString()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public String getAsText() {

        Object obj = getValue();
        Method method = null;
        try {
            method = clazz.getMethod("name", null);
            return method.invoke(obj).toString();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }
}

