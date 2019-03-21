package com.example.user.isotuapp.Model;


import java.io.Serializable;

public class Event implements Serializable {
    String eventId, image, judulEvent, tanggal, jamMulai, jamBerakhir, lokasi, deskripsi;

    public Event(){

    }

    public Event( String eventId, String image, String judulEvent, String tanggal, String jamMulai, String jamBerakhir, String lokasi, String deskripsi) {
        this.eventId = eventId;
        this.image = image;
        this.judulEvent = judulEvent;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamBerakhir = jamBerakhir;
        this.lokasi = lokasi;
        this.deskripsi = deskripsi;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getJudulEvent() {
        return judulEvent;
    }

    public void setJudulEvent(String judulEvent) {
        this.judulEvent = judulEvent;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(String jamMulai) {
        this.jamMulai = jamMulai;
    }

    public String getJamBerakhir() {
        return jamBerakhir;
    }

    public void setJamBerakhir(String jamBerakhir) {
        this.jamBerakhir = jamBerakhir;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
