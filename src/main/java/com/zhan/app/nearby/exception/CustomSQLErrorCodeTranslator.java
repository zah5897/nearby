package com.zhan.app.nearby.exception;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

public class CustomSQLErrorCodeTranslator extends SQLErrorCodeSQLExceptionTranslator {
	private static Logger log = Logger.getLogger(AppExceptionHandler.class);

	@SuppressWarnings("serial")
	@Override
	protected DataAccessException customTranslate(String task, String sql, SQLException sqlEx) {
		String errMsg = "task:\n" + task + "\nsql=\n" + sql;
		errMsg += "\n" + sqlEx.getMessage();
		log.error(errMsg);
		return new DataAccessException(errMsg) {
		};
	}

}
