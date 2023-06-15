package com.gfa.p2p.controllers;

import com.gfa.p2p.models.Log;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class DisplayController {
    @GetMapping("/")
    public String displayIndex(){
        Log.printLog(false,"/", "GET", "");
        return "main";
    }
    @GetMapping("/register")
    public String displayRegister(){
        Log.printLog(false,"/register", "GET", "");
        return "register";
    }
}
