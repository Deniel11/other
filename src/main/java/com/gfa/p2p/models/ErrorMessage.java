package com.gfa.p2p.models;

public class ErrorMessage {
    private String error;

    public ErrorMessage(String message) {
        this.error = message;
    }

    public String getError() {
        return error;
    }
}
