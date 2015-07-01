/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import tschuba.util.queries.TemporalType;

/**
 *
 * @author Thomas
 */
public class OracleSqlQueryFormatter extends NativeSqlQueryFormatterBase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("'DATE('''yyyy-MM-dd''')'");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("'TIME('''HH:mm:ss.SSSSSS''')'");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("'TIMESTAMP('''yyyy-MM-dd HH:mm:ss.SSSSSS''')'");

    @Override
    public DateFormat getFormatByType(TemporalType type) {
        switch (type) {
            case DATE:
                return DATE_FORMAT;
            case TIME:
                return TIME_FORMAT;
            case DATE_TIME:
                return DATE_TIME_FORMAT;
            default:
                throw new IllegalArgumentException();
        }
    }

}
