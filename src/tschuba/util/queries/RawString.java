/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

import java.util.Objects;

/**
 *
 * @author Thomas
 */
public class RawString {

    StringBuilder rawStringBuilder;

    public RawString() {
        this.rawStringBuilder = new StringBuilder();
    }

    public RawString(String str) {
        this.rawStringBuilder = new StringBuilder(str);
    }

    public void append(String str) {
        this.rawStringBuilder.append(str);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.rawStringBuilder);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RawString other = (RawString) obj;
        if (!Objects.equals(this.rawStringBuilder, other.rawStringBuilder)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return rawStringBuilder.toString();
    }

}