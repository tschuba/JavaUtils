/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

import java.sql.Time;
import java.sql.Timestamp;
import tschuba.util.queries.format.QueryFormatter;
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
import tschuba.util.queries.format.QueryLanguageFormatterBase;

/**
 * The QueryBuilder is intended to ease and simplify the creation, manipulation
 * and transformation of queries, i.e. JPQL or SQL.
 *
 * @author tsc
 */
public class QueryBuilder implements Parametrized {

    private static final JPQLQueryFormatter JPQL_FORMATTER = new JPQLQueryFormatter();

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
     * Extracts the original value if the specified value represents a wrapper
     * like {@link Temporal}, {@link In}, {@link RawString
     *
     * @param value the value to unwrap
     * @return the unwrapped value
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
     * Adds a rawValue value or statement to this builder.
     *
     * @param statement statement or rawValue value to add
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
     * Adds given value to the builder's components.
     *
     * @param value the value to add
     * @return query builder instance.
     */
    public QueryBuilder value(Object value) {
        this.components.add(value);
        return this;
    }

    /**
     * Adds a temporal value to the builder's components.
     *
     * @param date date value
     * @param type type of the temporal value.
     * @return query builder instance.
     */
    public QueryBuilder value(Date date, TemporalType type) {
        Temporal temporal = new Temporal(date, type);
        this.components.add(temporal);
        return this;
    }

    /**
     * Adds a timestamp to the builder's components by wrapping it into
     * {@link Temporal}.
     *
     * @param timestamp the timestamp
     * @return query builder instance.
     */
    public QueryBuilder value(Timestamp timestamp) {
        Temporal temporal = new Temporal(timestamp, TemporalType.DATE_TIME);
        this.components.add(temporal);
        return this;
    }

    /**
     * Adds a date to the builder's components by wrapping it into
     * {@link Temporal}.
     *
     * @param date the date.
     * @return query builder instance.
     */
    public QueryBuilder value(java.sql.Date date) {
        Temporal temporal = new Temporal(date, TemporalType.DATE);
        this.components.add(temporal);
        return this;
    }

    /**
     * Adds a time to the builder's components by wrapping it into
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

    public QueryBuilder in(Object... values) {
        List<Object> valueList = Arrays.asList(values);
        return this.in(valueList);
    }

    public QueryBuilder in(Iterable<?> values) {
        this.components.add(values);
        return this;
    }

    public QueryBuilder in(TemporalType type, Date... dates) {
        List<Date> dateList = Arrays.asList(dates);
        this.in(type, dateList);
        return this;
    }

    public QueryBuilder in(TemporalType type, Iterable<? extends Date> dates) {
        Temporals temporals = new Temporals(dates, type);
        this.components.add(temporals);
        return this;
    }

    public QueryBuilder param(int position, Object value) {
        this.positionalParameters.put(position, value);
        return this;
    }

    public QueryBuilder param(int position, Date date, TemporalType type) {
        Temporal temporal = new Temporal(date, type);
        this.positionalParameters.put(position, temporal);
        return this;
    }

    public QueryBuilder param(String name, Object value) {
        this.namedParameters.put(name, value);
        return this;
    }

    public QueryBuilder param(String name, Date date, TemporalType type) {
        Temporal temporal = new Temporal(date, type);
        this.namedParameters.put(name, date);
        return this;
    }

    @Override
    public Object param(int position) {
        Object parameter = this.positionalParameters.get(position);
        return this.unwrapParam(parameter);
    }

    @Override
    public Object param(String name) {
        Object parameter = this.namedParameters.get(name);
        return this.unwrapParam(parameter);
    }

    public QueryBuilder rawParam(int position, String value) {
        RawString rawString = new RawString(value);
        this.positionalParameters.put(position, rawString);
        return this;
    }

    public QueryBuilder rawParam(String name, String value) {
        RawString rawString = new RawString(value);
        this.namedParameters.put(name, rawString);
        return this;
    }

    @Override
    public Enumeration<String> paramNames() {
        Set<String> names = this.namedParameters.keySet();
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<Integer> paramPositions() {
        Set<Integer> positions = this.positionalParameters.keySet();
        return Collections.enumeration(positions);
    }

    @Override
    public boolean hasParam(int position) {
        return this.positionalParameters.containsKey(position);
    }

    @Override
    public boolean hasParam(String name) {
        return this.namedParameters.containsKey(name);
    }

    public QueryBuilder withPositionalParametersOnly() {
        PositionalParametersOnlyQueryConverter converter = new PositionalParametersOnlyQueryConverter();
        return converter.convert(this);
    }

    public QueryBuilder withNamedParametersOnly() {
        NamedParametersOnlyQueryConverter converter = new NamedParametersOnlyQueryConverter();
        return converter.convert(this);
    }

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

    public String jpql(boolean includeParameters) {
        JPQLQueryFormatter formatter = new JPQLQueryFormatter(includeParameters);

        String jpql = JPQL_FORMATTER.format(this);
        return jpql;
    }

    public <T> T format(QueryFormatter<T> formatter) {
        return formatter.format(this);
    }

    public static void main(String[] args) {
        QueryBuilder sourceBuilder = new QueryBuilder();
        sourceBuilder.rawValue("select col1 from table where col2=:col and col3=?2 amd col4=?");
        sourceBuilder.rawParam("col", "n1");
        sourceBuilder.rawParam(1, "p1");
        sourceBuilder.rawParam(2, "p2");
        PlainStringQueryFormatter sourceFormatter = new PlainStringQueryFormatter();
        System.out.println("Source: " + sourceFormatter.format(sourceBuilder));

        QueryBuilder withPositionalParametersOnly = sourceBuilder.withPositionalParametersOnly();
        String positionalOnly = withPositionalParametersOnly.sql(SqlDialect.MicrosoftSqlServer, true);
        System.out.println("Positional only: " + positionalOnly);

        QueryBuilder withNamedParametersOnly = sourceBuilder.withNamedParametersOnly();
        String namedOnly = withNamedParametersOnly.sql(SqlDialect.Oracle, true);
        System.out.println("Named only: " + namedOnly);
    }
}
