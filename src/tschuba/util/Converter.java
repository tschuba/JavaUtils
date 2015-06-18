/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util;

/**
 * Interface for converters.
 *
 * @author tsc
 * @param <V> type of the original value
 * @param <T> type of the converted value
 */
public interface Converter<V, T> {

    /**
     * Converts a value into target type.
     *
     * @param value the value to convert
     * @return the converted value
     */
    public T convert(V value);

    /**
     * Converts back to original type from converted value.
     *
     * @param value the value to convert back from
     * @return the original value
     */
    public V convertBack(T value);

}
