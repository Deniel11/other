package com.gfa.p2p.controllers;

import com.gfa.p2p.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
public class mainController {
    @PersistenceContext
    private EntityManager entityManager;
    private int lastMessageID;
    private boolean needChange = false;
    @Transactional
    @PostMapping("/save-message")
    public ResponseEntity<?> postMessage(@RequestBody Message message){
        if(message.equals(null)){
            ErrorMessage errorMessage = new ErrorMessage("Empty message");
            Log.printLog(true,"/save-message", "POST", errorMessage.getError());
            return new ResponseEntity<>(errorMessage,HttpStatus.NO_CONTENT);
        }
        saveMessage(message);
        Log.printLog(false,"/save-message", "POST", message.getText());
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @GetMapping("/messages")
    public ResponseEntity<?> displayMessages(){
        Query query = entityManager.createNativeQuery("SELECT * FROM message ORDER BY timestamp", Message.class);
        List<Message> messages = query.getResultList();
        if(messages.size() == 0){
            ErrorMessage errorMessage = new ErrorMessage("Empty database");
            Log.printLog(true,"/messages", "GET", errorMessage.getError());
            return new ResponseEntity<>(errorMessage,HttpStatus.NOT_FOUND);
        }
        Log.printLog(false,"/messages", "GET", "List of messages");
        return new ResponseEntity<>(messages,HttpStatus.OK);
    }
    @Transactional
    @PostMapping("/api/message/receive")
    public ResponseEntity<?> receiveNewMessage(@RequestBody Receive receive){
        Message newMessage = receive.getMessage();
        String missingFields = "";
        if(newMessage.getId() == 0){
            if(missingFields != ""){
                missingFields += ", ";
            }
            missingFields += "message.id";
        }
        if(newMessage.getUsername() == null){
            if(missingFields != ""){
                missingFields += ", ";
            }
            missingFields += "message.username";
        }
        if(newMessage.getText() == null){
            if(missingFields != ""){
                missingFields += ", ";
            }
            missingFields += "message.text";
        }
        if(newMessage.getTimestamp() == null){
            if(missingFields != ""){
                missingFields += ", ";
            }
            missingFields += "message.timestamp";
        }
        Client client = receive.getClient();
        if(client.getId() == null){
            if(missingFields != ""){
                missingFields += ", ";
            }
            missingFields += "client.id";
        }
        if(missingFields != ""){
            String returnText = "Missing field(s): ";
            returnText += missingFields;
            StatusError statusError = new StatusError("error", returnText);
            Log.printLog(true,"/api/message/receive", "POST", statusError.getMessage());
            return new ResponseEntity<>(statusError, HttpStatus.UNAUTHORIZED);
        }
        if(!System.getenv("CHAT_APP_UNIQUE_ID").equals(client.getId())){
//            receive.getClient().setId(System.getenv("CHAT_APP_UNIQUE_ID"));
//            receive.getMessage().setText("Belenyúltam :D");
            sendMessage(receive);
            saveMessage(newMessage);
            needChange = true;
        }
        Status status = new Status("ok");
        Log.printLog(false,"/api/message/receive", "POST", newMessage.getText());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/send-message")
    public ResponseEntity<?> sendMSG(){
        Message message = entityManager.find(Message.class, lastMessageID);
        if(message.equals(null)){
            ErrorMessage errorMessage = new ErrorMessage("Empty message");
            Log.printLog(true,"/send-message", "POST", errorMessage.getError());
            return new ResponseEntity<>(errorMessage,HttpStatus.NO_CONTENT);
        }
        Client client = new Client("CHAT_APP_UNIQUE_ID");
        sendMessage(new Receive(message,client));
        Log.printLog(false,"/send-message", "POST", message.getText());
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @GetMapping("/check-change")
    public ResponseEntity<Boolean> checkChange(){
        boolean output = needChange;
        needChange = false;
        return new ResponseEntity<>(output,HttpStatus.OK);
    }

    @GetMapping("/get-last-message")
    public ResponseEntity<?> getLastMessage(){
        return new ResponseEntity<>(entityManager.find(Message.class, lastMessageID), HttpStatus.OK);
    }
    public int generateRandomID(){
        Random random = new Random();
        return random.nextInt(1000000,9999999);
    }
    public void saveMessage(Message message){
        int randomID = generateRandomID();
        Message checkMessage = entityManager.find(Message.class, randomID);
        while (checkMessage != null){
            randomID = generateRandomID();
            checkMessage = entityManager.find(Message.class, randomID);
        }
        message.setId(randomID);
        lastMessageID = randomID;
        entityManager.persist(message);
    }

    public void sendMessage(Receive receive){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<Receive> entity = new HttpEntity<>(receive, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.exchange(System.getenv("CHAT_APP_PEER_ADDRESS"), HttpMethod.POST, entity, String.class);
        Log.printLog(false,System.getenv("CHAT_APP_PEER_ADDRESS"), "POST", result.getBody());
    }
}
