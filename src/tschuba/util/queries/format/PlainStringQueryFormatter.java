/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.format;

import java.text.DateFormat;
import tschuba.util.queries.TemporalType;

/**
 *
 * @author Thomas
 */
public class PlainStringQueryFormatter extends QueryLanguageFormatterBase {

    @Override
    public String format(Object object) {
        return "" + object;
    }

    @Override
    public DateFormat getFormatByType(TemporalType type) {
        throw new UnsupportedOperationException("Not supported!");
    }

}
