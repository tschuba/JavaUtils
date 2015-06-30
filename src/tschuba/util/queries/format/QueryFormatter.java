/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.format;

import tschuba.util.queries.QueryBuilder;

/**
 *
 * @author Thomas
 * @param <F>
 */
public interface QueryFormatter<F> {

    public F format(QueryBuilder builder);

}
