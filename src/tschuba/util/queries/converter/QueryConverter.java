/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.converter;

import tschuba.util.queries.QueryBuilder;

/**
 * Interface defining the public API to be implemented by QueryConverters. A
 * QueryConverter is intended to convert a QueryBuilder in a specific manner
 * creating a complete copy decoupling any dependencies to the original
 * {@link QueryBuilder}.
 *
 * @author Thomas
 */
public interface QueryConverter {

    /**
     * Converts a specified QueryBuilder.
     *
     * @param builder the QueryBuilder to convert.
     * @return the converted QueryBuilder.
     */
    public QueryBuilder convert(QueryBuilder builder);

}
