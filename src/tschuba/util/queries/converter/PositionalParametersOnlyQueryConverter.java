/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.converter;

import java.util.Enumeration;
import java.util.regex.Matcher;
import tschuba.util.queries.QueryBuilder;
import tschuba.util.queries.QueryBuilderConstants;
import tschuba.util.queries.wrapper.RawString;

/**
 * Converter to replace named with positional parameters in a QueryBuilder. All
 * named and positional parameters are reassigned by their position inside the
 * builder's query. Positional parameters probably do not retain their original
 * position! The {@link #convert(tschuba.util.queries.QueryBuilder)} method
 * creates a full copy of the QueryBuilder specified so the original
 * QueryBuilder won't get touched. For the other way round
 * {@link NamedParametersOnlyQueryConverter} can be used.
 *
 * @author Thomas
 */
public class PositionalParametersOnlyQueryConverter implements QueryConverter {

    @Override
    public QueryBuilder convert(QueryBuilder builder) {
        QueryBuilder builderClone = new QueryBuilder();
        int lastImplicitPositionalParameter = 0,
                positionalParameterCount = 0;
        final Enumeration<Object> components = builder.components();
        while (components.hasMoreElements()) {
            Object component = components.nextElement();
            if (component instanceof RawString) {
                String rawString = component.toString();
                Matcher matcher = QueryBuilderConstants.Parameter.PATTERN.matcher(rawString);
                StringBuffer replacement = new StringBuffer();
                while (matcher.find()) {
                    String parameter = rawString.substring(matcher.start(), matcher.end());

                    boolean hasParam;
                    Object value = null;
                    if (parameter.startsWith(QueryBuilderConstants.Parameter.PREFIX_NAMED)) {
                        String name = parameter.substring(1);
                        if (hasParam = builder.isBound(name)) {
                            value = builder.boundValue(name);
                        }
                    } else {
                        int position;
                        if (parameter.length() > 1) {
                            position = Integer.parseInt(parameter.substring(1));
                        } else {
                            position = lastImplicitPositionalParameter += 1;
                        }
                        if (hasParam = builder.isBound(position)) {
                            value = builder.boundValue(position);
                        }
                    }

                    if (hasParam) {
                        positionalParameterCount = positionalParameterCount += 1;
                        builderClone.bind(positionalParameterCount, value);
                    }

                    // replace current parameter with implicit positional parameter
                    matcher.appendReplacement(replacement, QueryBuilderConstants.Parameter.PREFIX_POSITIONAL);
                }
                matcher.appendTail(replacement);
                // replace current rawValue string with new string containing only positional parameters
                component = new RawString(replacement.toString());
            }
            builderClone.add(component);
        }
        return builderClone;
    }

}
