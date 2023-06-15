package com.gfa.p2p.controllers;

import com.gfa.p2p.models.ErrorMessage;
import com.gfa.p2p.models.Log;
import com.gfa.p2p.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class userController {
    @PersistenceContext
    private EntityManager entityManager;
    private User actualUser = null;
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        if (user.getName().isEmpty()) {
            ErrorMessage errorMessage = new ErrorMessage("Empty username input");
            Log.printLog(true, "/register", "POST", errorMessage.getError());
            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
        Query query = entityManager.createNativeQuery("SELECT * FROM user WHERE name = '" + user.getName() + "';", User.class);
        try {
            User checkUser = (User) query.getResultList().get(0);
            if (checkUser.getName().equals(user.getName())) {
                Log.printLog(false, "/register", "POST", "Login!");
                actualUser = checkUser;
                return new ResponseEntity<>(checkUser, HttpStatus.OK);
            }
        } catch (IndexOutOfBoundsException error){
            ErrorMessage errorMessage = new ErrorMessage(error.getMessage());
            Log.printLog(true, "/register", "POST", errorMessage.getError());
        }
        Log.printLog(false, "/register", "POST", user.getName());
        entityManager.persist(user);
        actualUser = user;
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/update/{actualName}")
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable String actualName) {
        if(user.getName().isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage("Empty username input");
            Log.printLog(true, "/update", "POST", errorMessage.getError());
            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
        Query query = entityManager.createNativeQuery("SELECT * FROM user WHERE name = '" + user.getName() + "';", User.class);
        try {
            User checkUser = (User) query.getResultList().get(0);
            if (checkUser.getName().equals(actualName)){
                ErrorMessage errorMessage = new ErrorMessage("This is your name!");
                Log.printLog(true, "/update", "PUT", errorMessage.getError());
                return new ResponseEntity<>(errorMessage, HttpStatus.OK);
            } else if (checkUser.getName().equals(user.getName())) {
                ErrorMessage errorMessage = new ErrorMessage("Existed name!");
                Log.printLog(true, "/update", "PUT", errorMessage.getError());
                return new ResponseEntity<>(errorMessage, HttpStatus.OK);
            }
        } catch (IndexOutOfBoundsException error){
            ErrorMessage errorMessage = new ErrorMessage(error.getMessage());
            Log.printLog(true, "/update", "PUT", errorMessage.getError());
        }
        User tmpUser = entityManager.find(User.class, user.getId());
        Log.printLog(false, "/update", "PUT", tmpUser.getName());
        tmpUser.setName(user.getName());
        entityManager.persist(tmpUser);
        actualUser = tmpUser;
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @GetMapping("/actual-user")
    public ResponseEntity<?> giveBackActualUser(){
        if(actualUser == null) {
            ErrorMessage errorMessage = new ErrorMessage("Please login");
            Log.printLog(true, "/actual-user", "GET", errorMessage.getError());
            return new ResponseEntity<>(errorMessage, HttpStatus.NO_CONTENT);
        }
        Log.printLog(true, "/actual-user", "GET", actualUser.getName());
        return new ResponseEntity<>(actualUser, HttpStatus.OK);
    }
}
