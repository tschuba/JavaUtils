/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.format;

import java.util.Enumeration;
import tschuba.util.queries.QueryBuilder;

/**
 *
 * @author Thomas
 */
public class PlainStringQueryFormatter implements QueryFormatter<String> {

    @Override
    public String format(QueryBuilder builder) {
        StringBuilder plainStringBuilder = new StringBuilder();
        Enumeration<Object> components = builder.components();
        while (components.hasMoreElements()) {
            final Object component = components.nextElement();
            plainStringBuilder.append(component);
        }
        // TODO: include parameters
        return plainStringBuilder.toString();
    }
    
}
