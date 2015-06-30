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
public class MsSqlServerFormatter extends NativeSqlFormatterBase {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("CONV'ERT('d'ate, ''MM/dd/yyyy'', 101)");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("CONV'ERT(time, ''HH:mm:ss.SSS'', 114)");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("CONV'ERT('d'atetime, ''yyyy-MM-dd HH:mm:ss.SSS'', 121)");

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
