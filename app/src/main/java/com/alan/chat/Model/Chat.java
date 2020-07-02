package com.alan.chat.Model;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String isSeen;
    private long timestamp = 0 ;

    public Chat(){

    }

    public Chat(String sender, String receiver, String message, String isSeen, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.isSeen = isSeen;

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
    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String isSeen() {
        return isSeen;
    }

    public void setSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
