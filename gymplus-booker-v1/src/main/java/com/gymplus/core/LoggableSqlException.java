package com.gymplus.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class LoggableSqlException extends SQLException {

    private static final Logger LOGGER = LogManager.getLogger(LoggableSqlException.class);

    /** DB-specific error code */
    public int sqlcode = 0;

    /** XOPEN SQLstate value, if available */
    public String sqlstate = null;

    /** SQL statement that caused the exception */
    public String statement = null;

    public LoggableSqlException(String statement, Throwable cause) {
        super(cause);
        if (cause instanceof SQLException) {
            SQLException se = ((SQLException)cause);
            this.sqlcode = se.getErrorCode();
            this.sqlstate = se.getSQLState();
        }
        this.statement = statement;
    }

    public static GMException toGMException(Throwable e) {
        SQLException esql = null;
        if (e instanceof LoggableSqlException && e.getCause() instanceof SQLException) {
            esql = (SQLException)e.getCause();
        } else if (e instanceof SQLException) {
            esql = (SQLException)e;
        }
        if (esql == null) { return null; }

        int sqlcode = esql.getErrorCode();
        if (sqlcode >= 20000 && sqlcode < 21000) {
            try {
                // Oracle error format is: ORA-20XXX: Here goes error message\n  ORA-YYY: suppressed exception1\n  ORA-ZZZ: suppressed exception2.. etc.
                // Code/message is taken from the inner SQL exception, but SQL statement (if any) is saved on outer LoggableSqlException, so we must preserve it 
                String message = esql.getMessage().trim().split("\\:|\n")[1].trim();
                int code = sqlcode - 20000;
                return new GMException(code, message, e);
            } catch (Exception ex) {
                LOGGER.error("Failed to parse SQL exception: {}", e.toString(), ex);
            }
        }
        return null;
    }

    private static final long serialVersionUID = -3217894947629127276L;
}
