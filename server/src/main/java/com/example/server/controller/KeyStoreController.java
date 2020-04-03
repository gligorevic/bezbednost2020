package com.example.server.controller;

import com.example.server.service.KeyStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class KeyStoreController {

    @Autowired
    private KeyStoreService keyStoreService;

}
