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
 * Converter to replace positional with named parameters in a QueryBuilder. All
 * positional parameters are reassigned by their position inside the builder's
 * query. The {@link #convert(tschuba.util.queries.QueryBuilder)} method creates
 * a full copy of the QueryBuilder specified so the original QueryBuilder won't
 * get touched. For the other way round
 * {@link PositionalParametersOnlyQueryConverter} can be used.
 *
 * @author Thomas
 */
public class NamedParametersOnlyQueryConverter implements QueryConverter {

    @Override
    public QueryBuilder convert(QueryBuilder builder) {
        QueryBuilder builderClone = new QueryBuilder();
        int implicitPositionalParameter = 0;
        final Enumeration<Object> components = builder.components();
        while (components.hasMoreElements()) {
            Object component = components.nextElement();
            if (component instanceof RawString) {
                String rawString = component.toString();
                Matcher matcher = QueryBuilderConstants.Parameter.PATTERN.matcher(rawString);
                StringBuffer replacement = new StringBuffer();
                while (matcher.find()) {
                    String parameter = rawString.substring(matcher.start(), matcher.end());
                    if (parameter.startsWith(QueryBuilderConstants.Parameter.PREFIX_POSITIONAL)) {
                        int position;
                        if (parameter.length() > 1) {
                            position = Integer.parseInt(parameter.substring(1));
                        } else {
                            position = implicitPositionalParameter += 1;
                        }
                        String name = QueryBuilderConstants.Parameter.PREFIX_POSITIONAL + position;
                        if (!builderClone.isBound(name) && builder.isBound(position)) {
                            Object value = builder.boundValue(position);
                            builderClone.bind(name, value);
                        }
                        matcher.appendReplacement(replacement, QueryBuilderConstants.Parameter.PREFIX_NAMED + name);
                    }
                }
                matcher.appendTail(replacement);
                component = new RawString(replacement.toString());
            }
            builderClone.add(component);
        }

        Enumeration<String> names = builder.boundNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            builderClone.bind(name, builder.boundValue(name));
        }

        return builderClone;
    }

}
