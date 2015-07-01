/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.converter;

import tschuba.util.queries.QueryBuilder;

/**
 *
 * @author Thomas
 */
public interface QueryConverter {

    public QueryBuilder convert(QueryBuilder builder);
    
}
