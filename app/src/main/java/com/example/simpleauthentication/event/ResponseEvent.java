package com.example.simpleauthentication.event;

public class ResponseEvent {
    public String responseString;
    public String STATUS;

    public ResponseEvent(String responseString, String STATUS) {
        this.responseString = responseString;
        this.STATUS = STATUS;
    }

    public String getResponseString() {
        return responseString;
    }

    public String getSTATUS() {
        return STATUS;
    }
}
