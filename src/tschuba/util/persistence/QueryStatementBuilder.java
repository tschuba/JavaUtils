/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.persistence;

import java.util.Collection;
import java.util.Date;

/**
 *
 * @author tsc
 */
public interface QueryStatementBuilder {

    public QueryStatementBuilder raw(String statement);
    
    public QueryStatementBuilder value(Object value);
    
    public QueryStatementBuilder value(Date date, TemporalType type);
    
    public QueryStatementBuilder in(Object... values);
    
    public QueryStatementBuilder in(Iterable<?> values);
    
    public QueryStatementBuilder in(TemporalType type, Date... dates);
    
    public QueryStatementBuilder in(TemporalType type, Iterable<? extends Date> dates);
    
    public QueryStatementBuilder equal(Object value);

    public QueryStatementBuilder param(int position, Object value);

    public QueryStatementBuilder param(int position, Date date, TemporalType type);

    public QueryStatementBuilder param(String name, Object value);

    public QueryStatementBuilder param(String name, Date date, TemporalType type);

    public QueryStatementBuilder rawParam(int position, String value);

    public QueryStatementBuilder rawParam(String name, String value);
    
    @Override
    public String toString();
}
