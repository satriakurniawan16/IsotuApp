package com.example.user.isotuapp.Model;

public class HobiModel {
    String idHobi;
    String hobi;
    int pos;

    public HobiModel() {
    }

    public HobiModel(String idHobi, String hobi, int pos) {
        this.idHobi = idHobi;
        this.hobi = hobi;
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getIdHobi() {
        return idHobi;
    }

    public void setIdHobi(String idHobi) {
        this.idHobi = idHobi;
    }

    public String getHobi() {
        return hobi;
    }

    public void setHobi(String hobi) {
        this.hobi = hobi;
    }
}
