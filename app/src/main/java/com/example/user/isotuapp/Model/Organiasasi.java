package com.example.user.isotuapp.Model;

public class Organiasasi {
    String id,nama;
    int pos;
    public Organiasasi() {
    }


    public Organiasasi(String id, String nama, int pos) {
        this.id = id;
        this.nama = nama;
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
