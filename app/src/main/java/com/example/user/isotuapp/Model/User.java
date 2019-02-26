package com.example.user.isotuapp.Model;

public class User {
    String username,email,image,fullname,nim,fakultas,jurusan,nohp,uid;
    int completeProfile;

    public User (){}

    public User(String username, String email, String image, String fullname, String nim, String fakultas, String jurusan, String nohp, String uid, int completeProfile) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.fullname = fullname;
        this.nim = nim;
        this.fakultas = fakultas;
        this.jurusan = jurusan;
        this.nohp = nohp;
        this.uid = uid;
        this.completeProfile = completeProfile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getFakultas() {
        return fakultas;
    }

    public void setFakultas(String fakultas) {
        this.fakultas = fakultas;
    }

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public int getCompleteProfile() {
        return completeProfile;
    }

    public void setCompleteProfile(int completeProfile) {
        this.completeProfile = completeProfile;
    }
}
