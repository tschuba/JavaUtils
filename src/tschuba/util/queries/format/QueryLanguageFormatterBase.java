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
import tschuba.util.queries.In;
import tschuba.util.queries.QueryBuilder;
import tschuba.util.queries.Temporal;
import tschuba.util.queries.TemporalType;

/**
 *
 * @author Thomas
 */
public abstract class QueryLanguageFormatterBase implements QueryFormatter<String> {

    @Override
    public String format(QueryBuilder builder) {
        StringBuilder sql = new StringBuilder();
        final Enumeration<Object> components = builder.components();
        if (components != null) {
            Object component;
            while (components.hasMoreElements()) {
                component = components.nextElement();
                String sqlComponent = this.formatComponent(component);
                sql.append(sqlComponent);
            }
        }
        return sql.toString();
    }

    public String formatComponent(Object component) {
        if (component instanceof Temporal) {
            Temporal temporal = (Temporal) component;
            return this.formatTemporal(temporal.getDate(), temporal.getType());

        } else if (component instanceof Timestamp) {
            return this.formatTemporal((Timestamp) component, TemporalType.DATE_TIME);

        } else if (component instanceof Time) {
            return this.formatTemporal((Time) component, TemporalType.TIME);

        } else if (component instanceof Date || component instanceof java.sql.Date) {
            return this.formatTemporal((Date) component, TemporalType.DATE);

        } else if (component instanceof String) {
            return "'" + component + "'";

        } else if (component instanceof In) {
            String formattedList = this.formatIterableComponent(((In) component).values());
            return " IN(" + formattedList + ")";

        } else if (component instanceof Iterable) {
            return this.formatIterableComponent((Iterable<Object>) component);

        } else if (component == null) {
            return this.formatNullComponent();

        } else {
            return "" + component;

        }
    }

    public String formatIterableComponent(Iterable<Object> component) {
        StringBuilder formattedComponent = new StringBuilder();
        for (Object element : component) {
            if (formattedComponent.length() > 0) {
                formattedComponent.append(",");
            }
            String formattedElement = this.formatComponent(element);
            formattedComponent.append(formattedElement);
        }
        return formattedComponent.toString();
    }

    public String formatNullComponent() {
        return " IS NULL";
    }
    
    public String formatTemporal(Date date, TemporalType type) {
        DateFormat formatter = this.getFormatByType(type);
        return formatter.format(date);
    }
    
    public abstract DateFormat getFormatByType(TemporalType type);

}
