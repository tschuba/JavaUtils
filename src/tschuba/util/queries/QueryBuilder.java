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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tschuba.util.queries.format.JPQLFormatter;
import tschuba.util.queries.format.MsSqlServerFormatter;
import tschuba.util.queries.format.OracleSqlFormatter;

/**
 *
 * @author tsc
 */
public class QueryBuilder implements Cloneable {

    private static final JPQLFormatter JPQL_FORMATTER = new JPQLFormatter();
    private static final MsSqlServerFormatter MS_SQL_FORMATTER = new MsSqlServerFormatter();
    private static final OracleSqlFormatter ORACLE_FORMATTER = new OracleSqlFormatter();

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

    /**
     * @return returns components of the
     */
    public Enumeration<Object> components() {
        return Collections.enumeration(components);
    }

    /**
     * Adds raw value/statement to this builder.
     *
     * @param statement statement or raw value to add
     * @return query builder instance
     */
    public QueryBuilder raw(String statement) {
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

    public QueryBuilder withPositionalParametersOnly() {
        QueryBuilder builderClone = new QueryBuilder();
        int lastImplicitPositionalParameter = 0;
        for (Object component : this.components) {
            if (component instanceof RawString) {
                String rawString = component.toString();
                Matcher matcher = PARAMETER_PATTERN.matcher(rawString);
                if (matcher.matches()) {
                    StringBuffer replacement = new StringBuffer();
                    while (matcher.find()) {
                        String parameter = matcher.group();
                        Object value;
                        if (parameter.startsWith(PARAMETER_PREFIX_NAMED)) {
                            String name = parameter.substring(1);
                            if (!namedParameters.containsKey(name)) {
                                throw new IllegalStateException("No entry for named parameter " + name);
                            }
                            value = namedParameters.get(name);
                        } else {
                            int position;
                            if (parameter.length() > 1) {
                                position = Integer.parseInt(parameter.substring(1));
                            } else {
                                position = lastImplicitPositionalParameter++;
                            }
                            if (!positionalParameters.containsKey(position)) {
                                throw new IllegalStateException("No entry for positional parameter " + position);
                            }
                            value = positionalParameters.get(position);
                        }

                        int position = builderClone.positionalParameters.size() + 1;
                        builderClone.positionalParameters.put(position, value);

                        // replace current parameter with implicit positional parameter
                        matcher.appendReplacement(replacement, PARAMETER_PREFIX_POSITIONAL);
                    }
                    matcher.appendTail(replacement);
                    // replace current raw string with new string containing only positional parameters
                    component = new RawString(replacement.toString());
                }
            }
            builderClone.components.add(component);
        }
        return builderClone;
    }

    public QueryBuilder withNamedParametersOnly() {
        QueryBuilder builderClone = new QueryBuilder();
        int implicitPositionalParameter = 0;
        for (Object component : this.components) {
            if (component instanceof RawString) {
                Matcher matcher = PARAMETER_PATTERN.matcher(component.toString());
                if (matcher.matches()) {
                    StringBuffer replacement = new StringBuffer();
                    while (matcher.find()) {
                        String parameter = matcher.group();
                        if (parameter.startsWith(PARAMETER_PREFIX_POSITIONAL)) {
                            int position;
                            if (parameter.length() > 1) {
                                position = Integer.parseInt(parameter.substring(1));
                            } else {
                                position = implicitPositionalParameter++;
                            }
                            String name = PARAMETER_PREFIX_POSITIONAL + position;
                            if (!builderClone.namedParameters.containsKey(name)) {
                                if (!this.positionalParameters.containsKey(position)) {
                                    throw new IllegalStateException("No entry for positional parameter " + position);
                                }
                                Object value = this.positionalParameters.get(position);
                                builderClone.namedParameters.put(PARAMETER_PREFIX_NAMED + name, value);
                            }
                            matcher.appendReplacement(replacement, name);
                        }
                    }
                    matcher.appendTail(replacement);
                    component = new RawString(replacement.toString());
                }
            }
            builderClone.components.add(component);
        }
        return builderClone;
    }

    public String sql(SqlDialect dialect) {
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

        String sql = formatter.format(this);
        return sql;
    }

    public String jpql() {
        String jpql = JPQL_FORMATTER.format(this);
        return jpql;
    }

    public <T> T format(QueryFormatter<T> formatter) {
        return formatter.format(this);
    }

    public static void main(String[] args) {
        QueryBuilder sourceBuilder = new QueryBuilder();
        sourceBuilder.raw("select col1 from table where col2=:col and col3=?2 amd col4=?");
        sourceBuilder.rawParam("col1", "n1");
        sourceBuilder.rawParam(1, "p1");
        sourceBuilder.rawParam(2, "p2");
        String source = sourceBuilder.sql(SqlDialect.Oracle);
        System.out.println("Source: " + source);

        QueryBuilder withPositionalParametersOnly = sourceBuilder.withPositionalParametersOnly();
        String namedOnly = withPositionalParametersOnly.sql(SqlDialect.Oracle);
        System.out.println("Named only: " + namedOnly);

        QueryBuilder withNamedParametersOnly = sourceBuilder.withNamedParametersOnly();
        String positionalOnly = withNamedParametersOnly.sql(SqlDialect.MicrosoftSqlServer);
        System.out.println("Positional only: " + positionalOnly);
    }
}
