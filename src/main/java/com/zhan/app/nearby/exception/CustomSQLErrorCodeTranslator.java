package com.zhan.app.nearby.exception;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

public class CustomSQLErrorCodeTranslator extends SQLErrorCodeSQLExceptionTranslator {
	private static Logger log = Logger.getLogger(AppExceptionHandler.class);

	@Override
	protected DataAccessException customTranslate(String task, String sql, SQLException sqlEx) {
		String errMsg = "task:" + task + "\\nsql=" + sql;
		log.error(errMsg);
		return new DataAccessException(errMsg) {
		};
	}

}
