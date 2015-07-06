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
 */
public abstract class NativeSqlQueryFormatterBase extends QueryLanguageFormatterBase {

    public NativeSqlQueryFormatterBase() {
    }

    public NativeSqlQueryFormatterBase(boolean includeParameters) {
        super(includeParameters);
    }

    @Override
    public String format(QueryBuilder builder) {
        // transform named to positional parameters
        QueryBuilder withPositionParametersOnly = builder.withPositionalParametersOnly();
        return super.format(withPositionParametersOnly);
    }

}
