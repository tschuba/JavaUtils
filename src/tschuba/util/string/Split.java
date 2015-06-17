/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.string;

import java.util.regex.Pattern;

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
    public String[] forString(String str) {
        String regExForSplit = this.delimiter;
        if (!this.regEx) {
            regExForSplit = Pattern.quote(regExForSplit);
        }

        String[] tokens = str.split(regExForSplit, this.limit);
        return tokens;
    }

    /**
     *
     * @param str
     * @param delimiter
     * @return
     */
    public static String[] forString(String str, String delimiter) {
        Split split = new Split().withDelimiter(delimiter);
        return split.forString(str);
    }
}
