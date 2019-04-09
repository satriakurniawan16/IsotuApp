package com.example.user.isotuapp.Model;

public class User {
    String username,email,image,fullname,nim,fakultas,jurusan,nohp,asal,uid,status,search;
    int completeProfile,positionfakultas,positionjurusan,positionprovinsi;
    Double latitude,longitude;

    public User (){}

    public User(String username, String email, String image, String fullname, String nim, String fakultas, String jurusan, String nohp, String asal, String uid, String status, String search, int completeProfile, Double latitude, Double longitude) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.fullname = fullname;
        this.nim = nim;
        this.fakultas = fakultas;
        this.jurusan = jurusan;
        this.nohp = nohp;
        this.asal = asal;
        this.uid = uid;
        this.status = status;
        this.search = search;
        this.completeProfile = completeProfile;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAsal() {
        return asal;
    }

    public void setAsal(String asal) {
        this.asal = asal;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
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

    public int getPositionfakultas() {
        return positionfakultas;
    }

    public void setPositionfakultas(int positionfakultas) {
        this.positionfakultas = positionfakultas;
    }

    public int getPositionjurusan() {
        return positionjurusan;
    }

    public void setPositionjurusan(int positionjurusan) {
        this.positionjurusan = positionjurusan;
    }

    public int getPositionprovinsi() {
        return positionprovinsi;
    }

    public void setPositionprovinsi(int positionprovinsi) {
        this.positionprovinsi = positionprovinsi;
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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", fullname='" + fullname + '\'' +
                ", nim='" + nim + '\'' +
                ", fakultas='" + fakultas + '\'' +
                ", jurusan='" + jurusan + '\'' +
                ", nohp='" + nohp + '\'' +
                ", asal='" + asal + '\'' +
                ", uid='" + uid + '\'' +
                ", status='" + status + '\'' +
                ", search='" + search + '\'' +
                ", completeProfile=" + completeProfile +
                '}';
    }
}
