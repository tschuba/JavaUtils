/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries;

import java.util.Date;

/**
 *
 * @author Thomas
 */
public class Temporals {

    private final Iterable<? extends Date> dates;
    private final TemporalType type;

    public Temporals(Iterable<? extends Date> dates, TemporalType type) {
        this.dates = dates;
        this.type = type;
    }

    public Iterable<? extends Date> getDates() {
        return dates;
    }

    public TemporalType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Temporals{" + "dates=" + dates + ", type=" + type + '}';
    }

}
