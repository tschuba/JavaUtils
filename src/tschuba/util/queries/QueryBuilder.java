/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

import tschuba.util.queries.wrapper.Wrapper;
import tschuba.util.queries.wrapper.Temporal;
import tschuba.util.queries.wrapper.RawString;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import tschuba.util.queries.converter.NamedParametersOnlyQueryConverter;
import tschuba.util.queries.converter.PositionalParametersOnlyQueryConverter;
import tschuba.util.queries.format.JPQLQueryFormatter;
import tschuba.util.queries.format.MsSqlServerQueryFormatter;
import tschuba.util.queries.format.OracleSqlQueryFormatter;
import tschuba.util.queries.format.PlainStringQueryFormatter;
import tschuba.util.queries.format.QueryFormatter;
import tschuba.util.queries.format.QueryLanguageFormatterBase;

/**
 * The QueryBuilder is intended to ease and simplify the creation, manipulation
 * and transformation of queries, i.e. JPQL or SQL. It manages components/parts
 * of a query and value bindings.
 * <br/>
 * Output formatting is done by formatters implementing {@link QueryFormatter}.
 *
 * @author tsc
 */
public class QueryBuilder {

    private final List<Object> components = new ArrayList<>();
    private final Map<String, Object> namedParameters = new LinkedHashMap<>();
    private final Map<Integer, Object> positionalParameters = new TreeMap<>();

    /**
     * @return returns the last element of the query's components list or null
     * if the components list is empty.
     */
    private Object lastComponent() {
        if (this.components.isEmpty()) {
            return null;
        } else {
            int lastIndex = components.size() - 1;
            return this.components.get(lastIndex);
        }
    }

    /**
     * Extracts the original add if the specified add represents a wrapper like {@link Temporal}, {@link In}, {@link RawString
     *
     * @param value the add to unwrap
     * @return the unwrapped add
     */
    private Object unwrapParam(Object value) {
        if (value instanceof Wrapper) {
            return ((Wrapper) value).unwrap();
        } else {
            return value;
        }
    }

    /**
     * @return returns an enumeration of the builder's components. Each
     * component can be a raw string or any object.
     */
    public Enumeration<Object> components() {
        return Collections.enumeration(components);
    }

    /**
     * Adds a rawValue add or statement to this builder.
     *
     * @param statement statement or rawValue add to add
     * @return query builder instance
     */
    public QueryBuilder rawValue(String statement) {
        Object lastComponent = this.lastComponent();
        if (lastComponent != null && lastComponent instanceof RawString) {
            ((RawString) lastComponent).append(statement);
        } else {
            RawString rawString = new RawString(statement);
            this.components.add(rawString);
        }
        return this;
    }

    /**
     * Adds a add to the builder's components.
     *
     * @param value the add to add
     * @return query builder instance.
     */
    public QueryBuilder add(Object value) {
        this.components.add(value);
        return this;
    }

    /**
     * Adds a value of temporal type to the builder's components.
     *
     * @param date date add
     * @param type type of the temporal add.
     * @return query builder instance.
     */
    public QueryBuilder add(Date date, TemporalType type) {
        Temporal temporal = new Temporal(date, type);
        this.components.add(temporal);
        return this;
    }

    /**
     * Adds a timestamp value to the builder's components by wrapping it into
     * {@link Temporal}.
     *
     * @param timestamp the timestamp
     * @return query builder instance.
     */
    public QueryBuilder add(Timestamp timestamp) {
        Temporal temporal = new Temporal(timestamp, TemporalType.DATE_TIME);
        this.components.add(temporal);
        return this;
    }

    /**
     * Adds a date value to the builder's components by wrapping it into
     * {@link Temporal}.
     *
     * @param date the date.
     * @return query builder instance.
     */
    public QueryBuilder add(java.sql.Date date) {
        Temporal temporal = new Temporal(date, TemporalType.DATE);
        this.components.add(temporal);
        return this;
    }

    /**
     * Adds a time value to the builder's components by wrapping it into
     * {@link Temporal}.
     *
     * @param time the time.
     * @return query builder instance.
     */
    public QueryBuilder value(Time time) {
        Temporal temporal = new Temporal(time, TemporalType.TIME);
        this.components.add(temporal);
        return this;
    }

    /**
     * Binds a value to a positional parameter.
     *
     * @param position the positional parameter's position
     * @param value the add to set for positional parameter
     * @return query builder instance
     */
    public QueryBuilder bind(int position, Object value) {
        this.positionalParameters.put(position, value);
        return this;
    }

    /**
     * Binds a value of temporal type to a positional parameter.
     *
     * @param position the positional parameter's position
     * @param date the add
     * @param type the add's temporal type of the new add
     * @return query builder instance
     */
    public QueryBuilder bind(int position, Date date, TemporalType type) {
        Temporal temporal = new Temporal(date, type);
        this.positionalParameters.put(position, temporal);
        return this;
    }

    /**
     * Binds a value to a named parameter.
     *
     * @param name the named parameter's name
     * @param value the add to set
     * @return query builder instance
     */
    public QueryBuilder bind(String name, Object value) {
        this.namedParameters.put(name, value);
        return this;
    }

    /**
     * Binds a value of temporal type to a named parameter.
     *
     * @param name the parameter's name
     * @param date the temporal add
     * @param type the temporal add's type
     * @return query builder instance
     */
    public QueryBuilder bind(String name, Date date, TemporalType type) {
        Temporal temporal = new Temporal(date, type);
        this.namedParameters.put(name, temporal);
        return this;
    }

    /**
     * Gets the value bound to a positional parameter.
     *
     * @param position the parameter's position
     * @return add bound to the position
     */
    public Object boundValue(int position) {
        Object parameter = this.positionalParameters.get(position);
        return this.unwrapParam(parameter);
    }

    /**
     * Gets the value bound to a named parameter.
     *
     * @param name the parameter's name
     * @return add bound to the name
     */
    public Object boundValue(String name) {
        Object parameter = this.namedParameters.get(name);
        return this.unwrapParam(parameter);
    }

    /**
     * Binds a raw string add to a positional parameter. Using this method to
     * bind the value prevents it from parsed as string by formatters.
     *
     * @param position the parameter's position
     * @param value the raw add
     * @return query builder instance
     */
    public QueryBuilder bindRaw(int position, String value) {
        RawString rawString = new RawString(value);
        this.positionalParameters.put(position, rawString);
        return this;
    }

    /**
     * Binds a raw string add to a parameter's name. Using this method to bind
     * the value prevents it from parsed as string by formatters.
     *
     * @param name the parameter's name
     * @param value the raw string add
     * @return query builder instance
     */
    public QueryBuilder bindRaw(String name, String value) {
        RawString rawString = new RawString(value);
        this.namedParameters.put(name, rawString);
        return this;
    }

    /**
     * @return returns an enumeration of names values were bound to.
     */
    public Enumeration<String> boundNames() {
        Set<String> names = this.namedParameters.keySet();
        return Collections.enumeration(names);
    }

    /**
     * @return returns an enumeration of positions values were bound to.
     */
    public Enumeration<Integer> boundPositions() {
        Set<Integer> positions = this.positionalParameters.keySet();
        return Collections.enumeration(positions);
    }

    /**
     * @param position position to check for bound value
     * @return returns {@code true} if a value is bound to the specified positon
     */
    public boolean isBound(int position) {
        return this.positionalParameters.containsKey(position);
    }

    /**
     *
     * @param name name to check for bound value
     * @return returns {@code true} if a value is bound to the specified name
     */
    public boolean isBound(String name) {
        return this.namedParameters.containsKey(name);
    }

    /**
     * @return returns a copy of this query builder with named parameters
     * replaced by positional parameters.
     * @see PositionalParametersOnlyQueryConverter
     */
    public QueryBuilder withPositionalParametersOnly() {
        PositionalParametersOnlyQueryConverter converter = new PositionalParametersOnlyQueryConverter();
        return converter.convert(this);
    }

    /**
     * @return returns a copy of this query builder with positional parameters
     * replaced by named parameters.
     * @see NamedParametersOnlyQueryConverter
     */
    public QueryBuilder withNamedParametersOnly() {
        NamedParametersOnlyQueryConverter converter = new NamedParametersOnlyQueryConverter();
        return converter.convert(this);
    }

    /**
     * Creates a native SQL statement based on this query builder using the
     * specified dialect.
     *
     * @param dialect SQL dialect to use for output
     * @param includeParameters if {@code true} parameters are replaced with
     * their respective bound values
     * @return native SQL statement
     * @see MsSqlServerQueryFormatter
     * @see OracleSqlQueryFormatter
     */
    public String sql(SqlDialect dialect, boolean includeParameters) {
        QueryLanguageFormatterBase formatter;
        if (dialect == null) {
            throw new IllegalArgumentException("No dialect specified");
        } else if (SqlDialect.MicrosoftSqlServer.equals(dialect)) {
            formatter = new MsSqlServerQueryFormatter();
        } else if (SqlDialect.Oracle.equals(dialect)) {
            formatter = new OracleSqlQueryFormatter();
        } else {
            throw new IllegalArgumentException("Unknown SQL Dialact " + dialect.name());
        }

        formatter.setIncludeParameters(includeParameters);
        return formatter.format(this);
    }

    /**
     * Creates a statement from this query builder using JPQL formats.
     *
     * @param includeParameters if {@code true} parameters are replaced with
     * their respective bound values
     * @return JPQL statement
     * @see JPQLQueryFormatter
     */
    public String jpql(boolean includeParameters) {
        JPQLQueryFormatter formatter = new JPQLQueryFormatter(includeParameters);
        return formatter.format(this);
    }

    /**
     * Creates a plain string from this query builder.
     *
     * @param includeParameters if {@code true} parameters are replaced with
     * their respective bound values
     * @return plain string
     * @see PlainStringQueryFormatter
     */
    public String toPlainString(boolean includeParameters) {
        PlainStringQueryFormatter formatter = new PlainStringQueryFormatter(includeParameters);
        return formatter.format(this);
    }

    public static void main(String[] args) {
        QueryBuilder sourceBuilder = new QueryBuilder();
        sourceBuilder.rawValue("select col1 from table where col2=:col and col3=?2 amd col4=?");
        sourceBuilder.bindRaw("col", "n1");
        sourceBuilder.bindRaw(1, "p1");
        sourceBuilder.bindRaw(2, "p2");
        PlainStringQueryFormatter sourceFormatter = new PlainStringQueryFormatter();
        System.out.println("Source: " + sourceFormatter.format(sourceBuilder));

        QueryBuilder withPositionalParametersOnly = sourceBuilder.withPositionalParametersOnly();
        String positionalOnly = withPositionalParametersOnly.toPlainString(false);
        System.out.println("Positional only: " + positionalOnly);

        QueryBuilder withNamedParametersOnly = sourceBuilder.withNamedParametersOnly();
        String namedOnly = withNamedParametersOnly.toPlainString(false);
        System.out.println("Named only: " + namedOnly);
    }
}
