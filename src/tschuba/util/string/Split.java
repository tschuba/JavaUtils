/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.string;

import java.lang.reflect.Array;
import java.util.regex.Pattern;
import tschuba.util.Converter;

/**
 *
 * @author tsc
 */
public class Split {

    private String delimiter;
    private boolean regEx;
    private int limit;

    /**
     *
     */
    public Split() {
    }

    /**
     *
     * @param delimiter
     * @return
     */
    public Split withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     *
     * @param regEx
     * @return
     */
    public Split asRegEx(boolean regEx) {
        this.regEx = regEx;
        return this;
    }

    /**
     *
     * @param limit
     * @return
     */
    public Split withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     *
     * @param str
     * @return
     */
    public String[] split(String str) {
        String regExForSplit = this.delimiter;
        if (!this.regEx) {
            regExForSplit = Pattern.quote(regExForSplit);
        }

        String[] tokens = str.split(regExForSplit, this.limit);
        return tokens;
    }

    /**
     *
     * @param <T>
     * @param str
     * @param converter
     * @return
     */
    public <T> T[] splitAndConvert(String str, Converter<String, T> converter) {
        String[] values = this.split(str);
        Object[] convertedValues = null;
        if (values != null) {
            convertedValues = new Object[values.length];
            for (int index = 0; index < values.length; index++) {
                convertedValues[index] = converter.convert(values[index]);
            }
        }
        return (T[]) convertedValues;
    }

    /**
     *
     * @param str
     * @param delimiter
     * @return
     */
    public static String[] split(String str, String delimiter) {
        Split split = new Split().withDelimiter(delimiter);
        return split.split(str);
    }
}
