/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tschuba.util.queries.wrapper;

import java.util.Date;
import java.util.Objects;
import tschuba.util.queries.TemporalType;

/**
 *
 * @author Thomas
 */
public class Temporal implements Wrapper<Date> {

    private final Date date;
    private final TemporalType type;

    public Temporal(Date date, TemporalType type) {
        this.date = date;
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public TemporalType getType() {
        return type;
    }

    @Override
    public Date unwrap() {
        return date;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.date);
        hash = 29 * hash + Objects.hashCode(this.type);
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
        final Temporal other = (Temporal) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Temporal{" + "date=" + date + ", type=" + type + '}';
    }
}
