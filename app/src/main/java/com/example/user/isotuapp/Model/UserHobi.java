package com.example.user.isotuapp.Model;

public class UserHobi {
    String iduser,fotoprofil,namaprofil;

    public UserHobi() {
    }

    public UserHobi(String iduser, String fotoprofil, String namaprofil) {
        this.iduser = iduser;
        this.fotoprofil = fotoprofil;
        this.namaprofil = namaprofil;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getFotoprofil() {
        return fotoprofil;
    }

    public void setFotoprofil(String fotoprofil) {
        this.fotoprofil = fotoprofil;
    }

    public String getNamaprofil() {
        return namaprofil;
    }

    public void setNamaprofil(String namaprofil) {
        this.namaprofil = namaprofil;
    }
}
