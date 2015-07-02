/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

import java.util.Enumeration;

/**
 *
 * @author Thomas
 */
public interface Parametrized {
    public Enumeration<String> paramNames();
    
    public Enumeration<Integer> paramPositions();
    
    public Object param(String name);
    
    public Object param(int position);
    
    public boolean hasParam(String name);
    
    public boolean hasParam(int position);
}
