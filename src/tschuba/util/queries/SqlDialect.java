/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 *
 * @author Thomas
 */
public enum SqlDialect {

    MicrosoftSqlServer("Microsoft SQL Server"), Oracle("Oracle"), PostgreSQL("PostgreSQL"), MySQL("MySQL"), HSQLDB("HSQL Database Engine"), Unknown();

    private String databaseProductName;

    private SqlDialect() {
    }

    private SqlDialect(String databaseProductName) {
        this.databaseProductName = databaseProductName;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public static SqlDialect fromConnection(Connection connection) {
        String databaseProductName;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            databaseProductName = metaData.getDatabaseProductName();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        for (SqlDialect dialect : SqlDialect.values()) {
            if (dialect.databaseProductName != null && dialect.databaseProductName.equals(databaseProductName)) {
                return dialect;
            }
        }
        return SqlDialect.Unknown;
    }
}
