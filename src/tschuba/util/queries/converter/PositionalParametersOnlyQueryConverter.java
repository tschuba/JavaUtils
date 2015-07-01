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
import tschuba.util.queries.RawString;

/**
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
                    Object value;
                    if (parameter.startsWith(QueryBuilderConstants.Parameter.PREFIX_NAMED)) {
                        String name = parameter.substring(1);
                        if (!builder.hasParam(name)) {
                            throw new IllegalStateException("No entry for named parameter " + name);
                        }
                        value = builder.param(name);
                    } else {
                        int position;
                        if (parameter.length() > 1) {
                            position = Integer.parseInt(parameter.substring(1));
                        } else {
                            position = lastImplicitPositionalParameter += 1;
                        }
                        if (!builder.hasParam(position)) {
                            throw new IllegalStateException("No entry for positional parameter " + position);
                        }
                        value = builder.param(position);
                    }

                    positionalParameterCount = positionalParameterCount += 1;
                    builderClone.param(positionalParameterCount, value);

                    // replace current parameter with implicit positional parameter
                    matcher.appendReplacement(replacement, QueryBuilderConstants.Parameter.PREFIX_POSITIONAL);
                }
                matcher.appendTail(replacement);
                // replace current rawValue string with new string containing only positional parameters
                component = new RawString(replacement.toString());
            }
            builderClone.value(component);
        }
        return builderClone;
    }

}
