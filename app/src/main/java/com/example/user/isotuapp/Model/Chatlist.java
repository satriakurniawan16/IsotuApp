package com.example.user.isotuapp.Model;

import java.io.Serializable;

public class Chatlist implements Serializable {
    public String id;
    public String type;

    public Chatlist() {
    }

    public String getType() {
        return type;
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