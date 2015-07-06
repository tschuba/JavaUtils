/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.format;

import tschuba.util.queries.QueryBuilder;

/**
 * Interface to be implemented by implementations to format a QueryBuilder into
 * a specific output format.
 *
 * @author Thomas
 * @param <F> type of the output created from QueryBuilder input
 */
public interface QueryFormatter<F> {

    /**
     * Converts from one 
     * @param builder
     * @return 
     */
    public F format(QueryBuilder builder);

}
