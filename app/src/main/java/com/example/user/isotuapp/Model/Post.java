package com.example.user.isotuapp.Model;

import java.io.Serializable;

public class Post implements Serializable{
    User user;
    String postId,image,text,type ,idpost,iduser,imageuser,nameUser,captionshare;
    long numlikes,numComment,numshare,timeCreated,newtimeCreate;

    public Post() {
    }

    public Post(User user, String postId, String image, String text, String type, long numlikes, long numComment,long numshare, long timeCreated) {
        this.user = user;
        this.postId = postId;
        this.image = image;
        this.text = text;
        this.type = type;
        this.numlikes = numlikes;
        this.numComment = numComment;
        this.numshare = numshare;
        this.timeCreated = timeCreated;
    }

    public Post(User user, String postId, String image, String text, String type, long numlikes, long numComment, long numshare, long timeCreated, String idpost, String iduser, String imageuser, String nameUser, String captionshare,long newtimeCreate) {
        this.user = user;
        this.postId = postId;
        this.image = image;
        this.text = text;
        this.type = type;
        this.idpost = idpost;
        this.iduser = iduser;
        this.imageuser = imageuser;
        this.nameUser = nameUser;
        this.captionshare = captionshare;
        this.numlikes = numlikes;
        this.numComment = numComment;
        this.numshare = numshare;
        this.timeCreated = timeCreated;
        this.newtimeCreate = newtimeCreate;
    }

    public String getIdpost() {
        return idpost;
    }

    public void setIdpost(String idpost) {
        this.idpost = idpost;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getImageuser() {
        return imageuser;
    }

    public void setImageuser(String imageuser) {
        this.imageuser = imageuser;
    }

    public long getNewtimeCreate() {
        return newtimeCreate;
    }

    public void setNewtimeCreate(long newtimeCreate) {
        this.newtimeCreate = newtimeCreate;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getCaptionshare() {
        return captionshare;
    }

    public void setCaptionshare(String captionshare) {
        this.captionshare = captionshare;
    }

    public long getNumshare() {
        return numshare;
    }

    public void setNumshare(long numshare) {
        this.numshare = numshare;
    }

    public Post(User user, String postId, String text, String type, long numlikes, long numComment, long timeCreated) {
        this.user = user;
        this.postId = postId;
        this.text = text;
        this.type = type;
        this.numlikes = numlikes;
        this.numComment = numComment;
        this.timeCreated = timeCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getNumlikes() {
        return numlikes;
    }

    public void setNumlikes(long numlikes) {
        this.numlikes = numlikes;
    }

    public long getNumComment() {
        return numComment;
    }

    public void setNumComment(long numComment) {
        this.numComment = numComment;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
