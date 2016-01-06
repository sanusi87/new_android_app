package com.example.william.myapplication;

import java.io.Serializable;

public class Language implements Serializable {
    public int id;
    public String name;
    public int spoken;
    public int written;
    public int isNative;

    public Language( int id, String name ){
        this.id = id;
        this.name = name;
    }

    // need to to override these two method if if using indexOf
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final JobType other = (JobType) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }

        if (this.id != other.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 53 * hash + this.id;
        return hash;
    }
}
