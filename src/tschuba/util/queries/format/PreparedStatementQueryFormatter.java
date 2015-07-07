/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;
import tschuba.util.queries.QueryBuilder;
import tschuba.util.queries.SqlDialect;
import tschuba.util.queries.wrapper.Wrapper;

/**
 *
 * @author Thomas
 */
public class PreparedStatementQueryFormatter implements QueryFormatter<PreparedStatement> {

    private Connection connection;

    public PreparedStatementQueryFormatter(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Binds values to specified prepared statement.
     *
     * @param statement the statement
     * @param position the position/index
     * @param value the value to bind to the specified position/index
     * @throws SQLException
     */
    private void bindValue(PreparedStatement statement, int position, Object value) throws SQLException {
        if (value == null) {
            statement.setNull(position, Types.NULL);
        } else if (value instanceof Wrapper) {
            Object unwrappedValue = ((Wrapper) value).unwrap();
            value = unwrappedValue;
        }
        statement.setObject(position, value);
    }

    @Override
    public PreparedStatement format(QueryBuilder builder) {
        // prepared statements only support position parameters
        QueryBuilder withPositionalParameters = builder.withPositionalParametersOnly();

        // unbind certain values to include values not supported by prepared statements directly into the SQL statement
        TreeMap<Integer, Object> valueBindings = new TreeMap<>();
        Enumeration<Integer> boundPositions = withPositionalParameters.boundPositions();
        while (boundPositions.hasMoreElements()) {
            Integer position = boundPositions.nextElement();
            Object value = withPositionalParameters.boundValue(position);
            if (!(value instanceof Iterable)) {
                valueBindings.put(position, value);
                withPositionalParameters.unbind(position);
            }
        }

        try {
            // create prepared statement for SQL
            SqlDialect dialect = SqlDialect.fromConnection(connection);
            String sql = withPositionalParameters.sql(dialect, true);
            PreparedStatement statement = connection.prepareStatement(sql);

            // bind values supported by prepared statement
            Iterator<Object> valueIterator = valueBindings.values().iterator();
            for (int position = 1; valueIterator.hasNext(); position++) {
                Object value = valueIterator.next();
                this.bindValue(statement, position, value);
            }

            return statement;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
