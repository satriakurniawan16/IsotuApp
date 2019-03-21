package com.example.user.isotuapp.Model;

import java.io.Serializable;

public class Chatlist implements Serializable {
    public String id;

    public Chatlist() {
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
