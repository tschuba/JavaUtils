/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author tsc
 */
public abstract class QueryStatementBuilderBase implements QueryStatementBuilder {

    private StringBuilder rawStatement = new StringBuilder();
    private Map<String, Object> namedParameters = new LinkedHashMap<String, Object>();
    private Map<Integer, Object> positionalParameters = new TreeMap<Integer, Object>();
    private boolean dirty = false;

    protected abstract String convert(Object value);

    protected abstract String convert(Date value, TemporalType type);

    /**
     *
     * @param values
     * @return
     */
    protected String convert(Iterable<?> values) {
        if (values == null) {
            return null;
        }
        StringBuilder joinedValues = new StringBuilder();
        for (Object value : values) {
            if (joinedValues.length() > 0) {
                joinedValues.append(",");
            }
            String convertedValue = this.convert(value);
            joinedValues.append(convertedValue);
        }
        return joinedValues.toString();
    }

    protected String convert(Iterable<? extends Date> dates, TemporalType type) {
        if (dates == null) {
            return null;
        }
        StringBuilder joinedDates = new StringBuilder();
        for (Date date : dates) {
            if (joinedDates.length() > 0) {
                joinedDates.append(",");
            }
            String convertedDate = this.convert(date, type);
            joinedDates.append(convertedDate);
        }
        return joinedDates.toString();
    }

    @Override
    public QueryStatementBuilder raw(String statement) {
        this.rawStatement.append(statement);
        return this;
    }

    @Override
    public QueryStatementBuilder value(Object value) {
        String convertedValue = this.convert(value);
        if (convertedValue != null) {
            this.raw(convertedValue);
        }
        return this;
    }

    @Override
    public QueryStatementBuilder value(Date value, TemporalType type) {
        String convertedValue = this.convert(value, type);
        if (convertedValue != null) {
            this.raw(convertedValue);
        }
        return this;
    }

    @Override
    public QueryStatementBuilder in(Object... values) {

    }

    @Override
    public QueryStatementBuilder in(Iterable<?> values) {

    }

    @Override
    public QueryStatementBuilder in(TemporalType type, Date... dates) {
        if (dates != null && dates.length > 0) {
            this.raw(" IN (");
            for (int loop = 0; loop < dates.length; loop++) {
                if (loop > 0) {
                    this.raw(",");
                }
                Date date = dates[loop];
                this.value(date, type);
            }
            this.raw(")");
        }
        return this;
    }

    @Override
    public QueryStatementBuilder in(TemporalType type, Iterable<? extends Date> dates) {

    }

    @Override
    public QueryStatementBuilder equal(Object value) {

    }

    @Override
    public QueryStatementBuilder param(int position, Object value) {

    }

    @Override
    public QueryStatementBuilder param(int position, Date date, TemporalType type) {

    }

    @Override
    public QueryStatementBuilder param(String name, Object value) {

    }

    @Override
    public QueryStatementBuilder param(String name, Date date, TemporalType type) {

    }

    @Override
    public QueryStatementBuilder rawParam(int position, String value) {

    }

    @Override
    public QueryStatementBuilder rawParam(String name, String value) {

    }

}
