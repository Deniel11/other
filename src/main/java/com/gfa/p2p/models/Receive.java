package com.gfa.p2p.models;

public class Receive {
    private Message message;
    private Client client;

    public Receive(Message message, Client client) {
        this.message = message;
        this.client = client;
    }

    public Message getMessage() {
        return message;
    }


    public Client getClient() {
        return client;
    }
}
