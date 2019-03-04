package com.example.user.isotuapp.Model;

public class HobiModel {
    String idHobi;
    String hobi;

    public HobiModel() {
    }

    public HobiModel(String idHobi, String hobi) {
        this.idHobi = idHobi;
        this.hobi = hobi;
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
