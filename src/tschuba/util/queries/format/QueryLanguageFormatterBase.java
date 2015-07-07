/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.format;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import tschuba.util.queries.QueryBuilder;
import tschuba.util.queries.QueryBuilderConstants;
import tschuba.util.queries.wrapper.Temporal;
import tschuba.util.queries.TemporalType;

/**
 * Base class providing basic functionality for implementing formatters to
 * format query builders into string based query languages.
 *
 * @author Thomas
 */
public abstract class QueryLanguageFormatterBase implements QueryFormatter<String> {

    private boolean includeParameters;

    public QueryLanguageFormatterBase() {
    }

    public QueryLanguageFormatterBase(boolean includeParameters) {
        this.includeParameters = includeParameters;
    }

    public boolean isIncludeParameters() {
        return includeParameters;
    }

    public void setIncludeParameters(boolean includeParameters) {
        this.includeParameters = includeParameters;
    }

    @Override
    public String format(QueryBuilder builder) {
        // create SQL string from
        StringBuilder sqlBuilder = new StringBuilder();
        final Enumeration<Object> components = builder.components();
        if (components != null) {
            Object component;
            while (components.hasMoreElements()) {
                component = components.nextElement();
                String sqlComponent = this.format(component);
                sqlBuilder.append(sqlComponent);
            }
        }
        String sql = sqlBuilder.toString();
        if (isIncludeParameters()) {
            // replace parameters with bound values
            StringBuffer sqlBuffer = new StringBuffer();
            Matcher matcher = QueryBuilderConstants.Parameter.PATTERN.matcher(sql);
            int implicitPosition = 0;
            while (matcher.find()) {
                Object value;
                String parameter = sql.substring(matcher.start(), matcher.end());
                if (parameter.startsWith(QueryBuilderConstants.Parameter.PREFIX_NAMED)) {
                    String name = parameter.substring(QueryBuilderConstants.Parameter.PREFIX_NAMED.length());
                    if (!builder.isBound(name)) {
                        continue;
                    }
                    value = builder.boundValue(name);
                } else {
                    int minSize = QueryBuilderConstants.Parameter.PREFIX_POSITIONAL.length();
                    int position;
                    if (parameter.length() > minSize) {
                        position = Integer.parseInt(parameter.substring(minSize));
                    } else {
                        position = implicitPosition += 1;
                    }
                    if (!builder.isBound(position)) {
                        continue;
                    }
                    value = builder.boundValue(position);
                }
                // replace placeholder for parameter with formatted add
                String replacement = this.format(value);
                matcher.appendReplacement(sqlBuffer, replacement);
            }
            matcher.appendTail(sqlBuffer);
            sql = sqlBuffer.toString();
        }
        return sql;
    }

    /**
     * @param object object to format
     * @return returns the specified value formatted as string according to the
     * necessary formatting
     */
    public String format(Object object) {
        if (object instanceof Temporal) {
            Temporal temporal = (Temporal) object;
            return this.formatTemporal(temporal.getDate(), temporal.getType());

        } else if (object instanceof Timestamp) {
            return this.formatTemporal((Timestamp) object, TemporalType.DATE_TIME);

        } else if (object instanceof Time) {
            return this.formatTemporal((Time) object, TemporalType.TIME);

        } else if (object instanceof Date || object instanceof java.sql.Date) {
            return this.formatTemporal((Date) object, TemporalType.DATE);

        } else if (object instanceof String) {
            return "'" + object + "'";

        } else if (object instanceof Iterable) {
            return this.formatIterable((Iterable<Object>) object);

        } else if (object == null) {
            return this.formatNull();

        } else {
            return "" + object;

        }
    }

    /**
     * Formats an iterable collection of values by calling
     * {@link #format(java.lang.Object)} for each element and joins them to a
     * comma seperated list
     *
     * @param iterable iterable collection of values
     * @return comma seperated list of the elemnts' string value
     */
    public String formatIterable(Iterable<Object> iterable) {
        StringBuilder formattedComponent = new StringBuilder();
        for (Object element : iterable) {
            if (formattedComponent.length() > 0) {
                formattedComponent.append(",");
            }
            String formattedElement = this.format(element);
            formattedComponent.append(formattedElement);
        }
        return formattedComponent.toString();
    }

    /**
     * @return formats {@code null} values by returning "IS NULL";
     */
    public String formatNull() {
        return "IS NULL";
    }

    /**
     * Formats the specified temporal value using
     * {@link DateFormat#format(java.util.Date)}. The appropriate
     * {@link DateFormat} is retrieved by calling the implementation-specific
     * {@link #getFormatByType(tschuba.util.queries.TemporalType)}
     *
     * @param date the date to format
     * @param type temporal type of the date
     * @return string representation of the specified temporal value using
     * {@link DateFormat#format(java.util.Date)}
     */
    public String formatTemporal(Date date, TemporalType type) {
        DateFormat formatter = this.getFormatByType(type);
        return formatter.format(date);
    }

    /**
     * Gets the implementaton-specific {@link DateFormat} appropriate for
     * specified temporal type.
     *
     * @param type temporal type
     * @return returns date format to use for formatting.
     */
    public abstract DateFormat getFormatByType(TemporalType type);

}
