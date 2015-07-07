/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.wrapper;

import java.util.Date;
import tschuba.util.queries.TemporalType;

/**
 *
 * @author Thomas
 */
public class Temporals implements Wrapper<Iterable<? extends Date>> {

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
    public Iterable<? extends Date> unwrap() {
        return this.dates;
    }

    @Override
    public String toString() {
        return "Temporals{" + "dates=" + dates + ", type=" + type + '}';
    }

}
