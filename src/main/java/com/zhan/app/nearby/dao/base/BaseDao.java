package com.zhan.app.nearby.dao.base;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.zhan.app.nearby.dao.base.util.EntityPropertiesReflectUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BaseDao<T> {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    static final ConcurrentMap<Class<?>, BeanPropertyRowMapper> mapperCache = new ConcurrentHashMap(64);
    static final ConcurrentMap<Class<?>, ModelEntityPropertyWrapper> entityWrapperCache = new ConcurrentHashMap(64);
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    private String tableName;
    private Class<T> actualType;//真实类型

    public String getTableName() {
        return tableName;
    }

    public String getTableName(Class clazz) {
        if (entityWrapperCache.containsKey(clazz)) {
            return entityWrapperCache.get(clazz).getTableName();
        } else {
            return EntityPropertiesReflectUtil.getTableName(clazz);
        }
    }

    public BaseDao() {
        Type type = getClass().getGenericSuperclass();//获取UserDaoImpl<User>的参数化类型的父类BaseDaoImpl<User>
        if (type == null) {
            return;
        }
        //由于我们得到的是一个参数化类型, 所以转成ParameterizedType, 因为需要使用里面的方法
        ParameterizedType pt = (ParameterizedType) type;//强转
        Type[] actualTypeArr = pt.getActualTypeArguments();//获取真实参数类型数组[User.class], 可以调试看到这里的值
        actualType = (Class<T>) actualTypeArr[0];//数组只有一个元素
        tableName = EntityPropertiesReflectUtil.getTableName(actualType);
    }

    //泛型插入
    public int insert(T t) {
        ModelEntityPropertyWrapper wrapper = prepare(t.getClass());
        return insertObj(t, wrapper);
    }
    //任意entity 对象
    public int insertObject(Object o) {
        ModelEntityPropertyWrapper wrapper = prepare(o.getClass());
        return insertObj(o, wrapper);
    }
    public int insertColumns(String tableName,String[] columns,Object[] colVals) {
    	String sql = " insert into " + tableName + " (";
		String filedStr = "";
		for (String key : columns) {
			filedStr += (key + ",");
		}
		filedStr = filedStr.substring(0, filedStr.length() - 1);
		filedStr += " ) ";

		String values = " values ( ";
		for (Object object : colVals) {
			values += ("'" + object + "',");
		}
		values = values.substring(0, values.length() - 1);
		values += " ) ";

		sql += (filedStr + values);
		return jdbcTemplate.update(sql);
    	
    }

    public List<Object[]> praseBatchParam(String one, List<?> many) {
        List<Object[]> param = new ArrayList<>();
        for (Object o : many) {
            param.add(new Object[]{one, o});
        }
        return param;
    }



    private ModelEntityPropertyWrapper prepare(Class clazz) {
        ModelEntityPropertyWrapper wrapper = null;
        if (entityWrapperCache.containsKey(clazz)) {
            wrapper = entityWrapperCache.get(clazz);
        }
        if (wrapper == null) {
            wrapper = EntityPropertiesReflectUtil.createModelEntityPropertyWrapper(clazz);
            entityWrapperCache.put(clazz, wrapper);
        }
        return wrapper;
    }

    private int insertObj(Object o, ModelEntityPropertyWrapper wrapper) {
        final String sql = wrapper.praseSQL(o);
        logger.debug(sql);
        if (wrapper.isIDAutoIncrement()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    return ps;
                }
            }, keyHolder);
            long auto = keyHolder.getKey().longValue();
            try {
                if (wrapper.getIdFieldType().toString().equals("int")) {
                    EntityPropertiesReflectUtil.invokeSetMethod(o, wrapper.getIdField(), new Object[]{(int) auto}, int.class);
                } else {
                    EntityPropertiesReflectUtil.invokeSetMethod(o, wrapper.getIdField(), new Object[]{auto}, long.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        } else {
            return jdbcTemplate.update(sql);
        }
    }

    @SuppressWarnings("rawtypes")
	public BeanPropertyRowMapper getEntityMapper() {
        return getEntityMapper(actualType);
    }

    public BeanPropertyRowMapper getEntityMapper(Class actualType) {
        BeanPropertyRowMapper mapper = mapperCache.get(actualType);
        if (mapper != null) {
            return mapper;
        }
        prepare(actualType);
        mapper = new BeanPropertyRowMapper<T>(actualType) {
            @Override
            public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
                T t = super.mapRow(rs, rowNumber);
                entityWrapperCache.get(actualType).handleResultSet(t, rs, rowNumber);
                return t;
            }
        };
        mapperCache.put(actualType, mapper);
        return mapper;
    }

}
