package com.example.user.isotuapp.Model;

import java.io.Serializable;

public class Post implements Serializable {
    User user;
    String postId,image,text,type;
    long numlikes,numComment,timeCreated;

    public Post() {
    }

    public Post(User user, String postId, String image, String text, String type, long numlikes, long numComment, long timeCreated) {
        this.user = user;
        this.postId = postId;
        this.image = image;
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
