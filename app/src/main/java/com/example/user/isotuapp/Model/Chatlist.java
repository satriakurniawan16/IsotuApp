package com.example.user.isotuapp.Model;

import java.io.Serializable;

public class Chatlist implements Serializable {
    public String id;
    public String type;
    public String subtype;


    public Chatlist() {
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Chatlist(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}