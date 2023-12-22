package sg.izt.pokemonserver.controller;

import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import sg.izt.pokemonserver.model.Pokemon;
import sg.izt.pokemonserver.model.PokemonType;
import sg.izt.pokemonserver.service.ApiService;
import sg.izt.pokemonserver.service.PokemonService;

@RestController
@RequestMapping(path = "/api")
public class PokemonRestController {

    @Autowired
    PokemonService pokemonSvc;

    @Autowired
    ApiService apiSvc;

    @GetMapping(path="/team/{id}")
    public ResponseEntity getTeamAsJson(@PathVariable("id") String id){
        Map<String,Object> jsonObject = apiSvc.getTeamAsMap(id);
        String jsonObjectString = jsonObject.toString();
        ResponseEntity<Map> response = new ResponseEntity<Map>(jsonObject, HttpStatusCode.valueOf(200));
        //ResponseEntity response = new ResponseEntity(jsonObjectString, HttpStatusCode.valueOf(200));
        return response;
    }

    @GetMapping(path = "/test")
    public ResponseEntity testResponse(){
        Map<String,Object> testMap = apiSvc.testMap();
        ResponseEntity response = new ResponseEntity(testMap,HttpStatusCode.valueOf(201));
        return response;
    }

    
    // @PostMapping(path = "/search")
    // public String searchPokemon(@RequestBody MultiValueMap <String,String> form){
    //     String pokemonName = form.getFirst("pokemonname").toLowerCase();
    //     ResponseEntity<String> result =  pokemonService.getPokemon(pokemonName);
    //     return result.getBody();

    // }
}
