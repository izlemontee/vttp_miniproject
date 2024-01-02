package sg.izt.pokemonserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(path = "/")
public class LandingController {

    @GetMapping
    public String getLandingPage(HttpSession session){

        session.invalidate();
        
        return "index";
    }
    
}
