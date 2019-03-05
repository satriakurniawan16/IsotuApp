package com.example.user.isotuapp.Model;

public class Contact {

    String userid,nameuser,imageuser,majoruser,facultyuser;

    public Contact() {
    }

    public Contact(String userid, String nameuser, String imageuser, String majoruser, String facultyuser) {
        this.userid = userid;
        this.nameuser = nameuser;
        this.imageuser = imageuser;
        this.majoruser = majoruser;
        this.facultyuser = facultyuser;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNameuser() {
        return nameuser;
    }

    public void setNameuser(String nameuser) {
        this.nameuser = nameuser;
    }

    public String getImageuser() {
        return imageuser;
    }

    public void setImageuser(String imageuser) {
        this.imageuser = imageuser;
    }

    public String getMajoruser() {
        return majoruser;
    }

    public void setMajoruser(String majoruser) {
        this.majoruser = majoruser;
    }

    public String getFacultyuser() {
        return facultyuser;
    }

    public void setFacultyuser(String facultyuser) {
        this.facultyuser = facultyuser;
    }
}
