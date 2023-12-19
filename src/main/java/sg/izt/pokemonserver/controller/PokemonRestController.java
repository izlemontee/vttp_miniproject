package sg.izt.pokemonserver.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.izt.pokemonserver.service.PokemonService;

@RestController
@RequestMapping(path = "/pokemonrest")
public class PokemonRestController {

    @Autowired
    PokemonService pokemonService;


    
    // @PostMapping(path = "/search")
    // public String searchPokemon(@RequestBody MultiValueMap <String,String> form){
    //     String pokemonName = form.getFirst("pokemonname").toLowerCase();
    //     ResponseEntity<String> result =  pokemonService.getPokemon(pokemonName);
    //     return result.getBody();

    // }
}
