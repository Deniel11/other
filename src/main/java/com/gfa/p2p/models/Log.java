package com.gfa.p2p.models;

import java.sql.Timestamp;
import java.time.Instant;

public class Log {
    public static void printLog(boolean error, String path, String method, String requestData){
        String logLevel = System.getenv("CHAT_APP_LOGLEVEL");
        if(logLevel.equals("INFO")) {
            if(error) {
                System.err.println(Timestamp.from(Instant.now()) + " " + "ERROR" + " " + path + " " + method + " " + requestData);
            } else {
                System.out.println(Timestamp.from(Instant.now()) + " " + "INFO" + " " + path + " " + method + " " + requestData);
            }
        } else if(logLevel.equals("ERROR")){
            if(error) {
                System.err.println(Timestamp.from(Instant.now()) + " " + logLevel + " " + path + " " + method + " " + requestData);
            }
        }
    }
}
