package com.example.user.isotuapp.Model;

public class Grup {

    String idgrup,imagegrup,namagrup;

    public Grup() {
    }

    public Grup(String idgrup, String imagegrup, String namagrup) {
        this.idgrup = idgrup;
        this.imagegrup = imagegrup;
        this.namagrup = namagrup;
    }

    public String getIdgrup() {
        return idgrup;
    }

    public void setIdgrup(String idgrup) {
        this.idgrup = idgrup;
    }

    public String getImagegrup() {
        return imagegrup;
    }

    public void setImagegrup(String imagegrup) {
        this.imagegrup = imagegrup;
    }

    public String getNamagrup() {
        return namagrup;
    }

    public void setNamagrup(String namagrup) {
        this.namagrup = namagrup;
    }
}
