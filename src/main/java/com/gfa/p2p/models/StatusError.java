package com.gfa.p2p.models;

public class StatusError {
    private String status;
    private String message;

    public StatusError(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
