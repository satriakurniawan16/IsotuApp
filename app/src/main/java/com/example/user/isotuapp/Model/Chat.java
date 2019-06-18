package com.example.user.isotuapp.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    private String idpost;
    private String imagepost;
    private String userpost;
    private String type;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, boolean isseen, String idpost, String imagepost, String userpost, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.idpost = idpost;
        this.imagepost = imagepost;
        this.userpost = userpost;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdpost() {
        return idpost;
    }

    public void setIdpost(String idpost) {
        this.idpost = idpost;
    }

    public String getImagepost() {
        return imagepost;
    }

    public void setImagepost(String imagepost) {
        this.imagepost = imagepost;
    }

    public String getUserpost() {
        return userpost;
    }

    public void setUserpost(String userpost) {
        this.userpost = userpost;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}