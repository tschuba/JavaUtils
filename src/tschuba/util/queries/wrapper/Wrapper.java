/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.wrapper;

/**
 * Interface for type wrappers.
 *
 * @author Thomas
 * @param <T> type of the wrapped value
 */
public interface Wrapper<T> {

    /**
     *
     * @return returns unwrapped value.
     */
    public T unwrap();
}
