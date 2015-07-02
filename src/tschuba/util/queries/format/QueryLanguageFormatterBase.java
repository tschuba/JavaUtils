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
import tschuba.util.queries.In;
import tschuba.util.queries.QueryBuilder;
import tschuba.util.queries.QueryBuilderConstants;
import tschuba.util.queries.Temporal;
import tschuba.util.queries.TemporalType;

/**
 *
 * @author Thomas
 */
public abstract class QueryLanguageFormatterBase implements QueryFormatter<String> {

    @Override
    public String format(QueryBuilder builder, boolean includeParameters) {
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
        if (includeParameters) {
            StringBuffer sqlBuffer = new StringBuffer();
            Matcher matcher = QueryBuilderConstants.Parameter.PATTERN.matcher(sql);
            int implicitPosition = 0;
            while (matcher.find()) {
                Object value;
                String parameter = sql.substring(matcher.start(), matcher.end());
                if (parameter.startsWith(QueryBuilderConstants.Parameter.PREFIX_NAMED)) {
                    String name = parameter.substring(QueryBuilderConstants.Parameter.PREFIX_NAMED.length());
                    if (!builder.hasParam(name)) {
                        continue;
                    }
                    value = builder.param(name);
                } else {
                    int minSize = QueryBuilderConstants.Parameter.PREFIX_POSITIONAL.length();
                    int position;
                    if (parameter.length() > minSize) {
                        position = Integer.parseInt(parameter.substring(minSize));
                    } else {
                        position = implicitPosition += 1;
                    }
                    if (!builder.hasParam(position)) {
                        continue;
                    }
                    value = builder.param(position);
                }
                // replace placeholder for parameter with formatted value
                String replacement = this.format(value);
                matcher.appendReplacement(sqlBuffer, replacement);
            }
            matcher.appendTail(sqlBuffer);
            sql = sqlBuffer.toString();
        }
        return sql;
    }

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

        } else if (object instanceof In) {
            String formattedList = this.formatIterableComponent(((In) object).values());
            return " IN(" + formattedList + ")";

        } else if (object instanceof Iterable) {
            return this.formatIterableComponent((Iterable<Object>) object);

        } else if (object == null) {
            return this.formatNull();

        } else {
            return "" + object;

        }
    }

    public String formatIterableComponent(Iterable<Object> component) {
        StringBuilder formattedComponent = new StringBuilder();
        for (Object element : component) {
            if (formattedComponent.length() > 0) {
                formattedComponent.append(",");
            }
            String formattedElement = this.format(element);
            formattedComponent.append(formattedElement);
        }
        return formattedComponent.toString();
    }

    public String formatNull() {
        return " IS NULL";
    }

    public String formatTemporal(Date date, TemporalType type) {
        DateFormat formatter = this.getFormatByType(type);
        return formatter.format(date);
    }

    public abstract DateFormat getFormatByType(TemporalType type);

}
