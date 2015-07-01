/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

import java.util.regex.Pattern;

/**
 *
 * @author Thomas
 */
public class QueryBuilderConstants {

    public static class Parameter {
        
        public static final Pattern PATTERN = Pattern.compile("\\?\\d*|:[\\w?]*");
        public static final String PREFIX_NAMED = ":";
        public static final String PREFIX_POSITIONAL = "?";
        
    }
}
