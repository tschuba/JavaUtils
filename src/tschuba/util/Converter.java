/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util;

/**
 *
 * @author tsc
 */
public interface Converter<V, T> {

    /**
     *
     * @param value
     * @return
     */
    public T convert(V value);

}
