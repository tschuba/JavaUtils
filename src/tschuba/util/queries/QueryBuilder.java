/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

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
import java.util.regex.Pattern;
import tschuba.util.queries.converter.NamedParametersOnlyQueryConverter;
import tschuba.util.queries.converter.PositionalParametersOnlyQueryConverter;
import tschuba.util.queries.format.JPQLQueryFormatter;
import tschuba.util.queries.format.MsSqlServerQueryFormatter;
import tschuba.util.queries.format.OracleSqlQueryFormatter;

/**
 *
 * @author tsc
 */
public class QueryBuilder implements Parametrized {

    private static final JPQLQueryFormatter JPQL_FORMATTER = new JPQLQueryFormatter();
    private static final MsSqlServerQueryFormatter MS_SQL_FORMATTER = new MsSqlServerQueryFormatter();
    private static final OracleSqlQueryFormatter ORACLE_FORMATTER = new OracleSqlQueryFormatter();

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\?\\d*|:[\\w?]*");
    private static final String PARAMETER_PREFIX_NAMED = ":";
    private static final String PARAMETER_PREFIX_POSITIONAL = "?";

    private List<Object> components = new ArrayList<>();
    private Map<String, Object> namedParameters = new LinkedHashMap<>();
    private Map<Integer, Object> positionalParameters = new TreeMap<>();

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

    private Object unwrapParam(Object param) {
        if (param instanceof Temporal) {
            param = ((Temporal) param).getDate();
        }
        return param;
    }

    /**
     * @return returns components of the
     */
    public Enumeration<Object> components() {
        return Collections.enumeration(components);
    }

    /**
     * Adds rawValue value/statement to this builder.
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

    public QueryBuilder value(Object value) {
        this.components.add(value);
        return this;
    }

    public QueryBuilder value(Date date, TemporalType type) {
        Temporal temporal = new Temporal(date, type);
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

    public Object param(int position) {
        Object parameter = this.positionalParameters.get(position);
        return this.unwrapParam(parameter);
    }

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

    public Enumeration<String> paramNames() {
        Set<String> names = this.namedParameters.keySet();
        return Collections.enumeration(names);
    }

    public Enumeration<Integer> paramPositions() {
        Set<Integer> positions = this.positionalParameters.keySet();
        return Collections.enumeration(positions);
    }

    public boolean hasParam(int position) {
        return this.positionalParameters.containsKey(position);
    }

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
        QueryFormatter<String> formatter;
        if (dialect == null) {
            throw new IllegalArgumentException("No dialect specified");
        } else if (SqlDialect.MicrosoftSqlServer.equals(dialect)) {
            formatter = MS_SQL_FORMATTER;
        } else if (SqlDialect.Oracle.equals(dialect)) {
            formatter = ORACLE_FORMATTER;
        } else {
            throw new IllegalArgumentException("Unknown SQL Dialact " + dialect.name());
        }

        String sql = formatter.format(this, includeParameters);
        return sql;
    }

    public String jpql() {
        String jpql = JPQL_FORMATTER.format(this);
        return jpql;
    }

    public <T> T format(QueryFormatter<T> formatter, boolean includeParameters) {
        return formatter.format(this, includeParameters);
    }

    public static void main(String[] args) {
        QueryBuilder sourceBuilder = new QueryBuilder();
        String sourceSql = "select col1 from table where col2=:col and col3=?2 amd col4=?";
        sourceBuilder.rawValue(sourceSql);
        sourceBuilder.rawParam("col", "n1");
        sourceBuilder.rawParam(1, "p1");
        sourceBuilder.rawParam(2, "p2");
        System.out.println("Source: " + sourceSql);

        QueryBuilder withPositionalParametersOnly = sourceBuilder.withPositionalParametersOnly();
        String positionalOnly = withPositionalParametersOnly.sql(SqlDialect.MicrosoftSqlServer, false);
        System.out.println("Positional only: " + positionalOnly);

        QueryBuilder withNamedParametersOnly = sourceBuilder.withNamedParametersOnly();
        String namedOnly = withNamedParametersOnly.sql(SqlDialect.Oracle, false);
        System.out.println("Named only: " + namedOnly);
    }
}
