/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.string;

import java.util.regex.Pattern;
import tschuba.util.Converter;

/**
 * Utility class for splitting strings. Uses method chaining for configuration.
 *
 * @author tsc
 */
public class Splitter {

    private String delimiter;
    private boolean regEx;
    private int limit;
    private boolean trim;

    /**
     * Default constructor
     */
    public Splitter() {
    }

    /**
     * @param delimiter delimiter to use for splitting strings. Depending on
     * parameter {@link #regEx} it is treated as regular expression or as an
     * ordinary string.
     * @return the current instance for method chaining.
     */
    public Splitter withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * @param regEx if {@code true} the delimiter is treated as regular
     * expression, otherwise as ordinary string.
     * @return the current instance for method chaining.
     */
    public Splitter asRegEx(boolean regEx) {
        this.regEx = regEx;
        return this;
    }

    /**
     * @param limit sets the result threshold. For description see
     * {@link String#split(java.lang.String, int)}.
     * @return the current instance for method chaining.
     */
    public Splitter withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * @param trim set to {@code true} if each token of the strings to split
     * should be trimmed, otherwise {@code false}.
     * @return the current instance for method chaining.
     */
    public Splitter withTrim(boolean trim) {
        this.trim = trim;
        return this;
    }

    /**
     * Split the given string using current configuration.
     *
     * @param str the string to split
     * @return the tokens
     */
    public String[] split(String str) {
        String regExForSplit = this.delimiter;
        if (!this.regEx) {
            regExForSplit = Pattern.quote(regExForSplit);
        }

        String[] tokens = str.split(regExForSplit, this.limit);
        if (this.trim) {
            for (int index = 0; index < tokens.length; index++) {
                tokens[index] = tokens[index].trim();
            }
        }
        return tokens;
    }

    /**
     * Splits given string calling {@link #split(java.lang.String)}.
     * Additionally every token gets converted calling the
     * {@link Converter#convert(java.lang.Object)} function of given converter.
     *
     * @param <T> type tokens are converted into
     * @param str the string to split
     * @param converter converter to apply to each token
     * @return the converted tokens
     */
    public <T> T[] splitAndConvert(String str, Converter<String, T> converter) {
        String[] values = this.split(str);
        Object[] convertedValues = null;
        if (values != null && converter != null) {
            convertedValues = new Object[values.length];
            for (int index = 0; index < values.length; index++) {
                convertedValues[index] = converter.convert(values[index]);
            }
        }
        return (T[]) convertedValues;
    }

    /**
     * Convenience method for splitting a string using a delimiter treated as
     * ordinary string. Just like calling {@link String#split(java.lang.String)}
     * with quoted delimiter.
     *
     * @param str the string to split
     * @param delimiter delimiter to use for splitting
     * @return the tokens
     */
    public static String[] split(String str, String delimiter) {
        Splitter split = new Splitter().withDelimiter(delimiter);
        return split.split(str);
    }
}
