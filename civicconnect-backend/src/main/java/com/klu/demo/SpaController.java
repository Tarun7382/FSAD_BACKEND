package com.klu.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping(value = {
            "/", "/login", "/signup", "/home", "/dashboard",
            "/moderator", "/politician", "/admin", "/feedback"
    })
    public String forward() {
        return "forward:/index.html";
    }
}