/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

/**
 *
 * @author Thomas
 */
public class In {
    
    private final Iterable<Object> values;

    public In(Iterable<Object> values) {
        this.values = values;
    }
    
    public Iterable<Object> values() {
        return this.values;
    }
    
}
