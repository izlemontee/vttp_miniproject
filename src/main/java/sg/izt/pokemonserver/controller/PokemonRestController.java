package sg.izt.pokemonserver.controller;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import sg.izt.pokemonserver.model.Pokemon;

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

    @GetMapping(path = "/generate")
    public ResponseEntity getPokemonFromParam(@RequestParam(name = "team") String teamParam){
        String[] teamArray = teamParam.split("\\+");
        if (teamArray.length>6){
            ResponseEntity<String> response = new ResponseEntity<String>("Cannot have more than 6 Pokemon.",HttpStatusCode.valueOf(400));
            return response;
        }
        try{
            List<Pokemon> teamList = apiSvc.generateTeam(teamArray);
            List<Float> typeList = pokemonSvc.calculateTeam(teamList);
            Map<String,Object> completeMap = apiSvc.generateTeamFromScratch(teamList, typeList);
            ResponseEntity<Map> response = new ResponseEntity<Map>(completeMap, HttpStatusCode.valueOf(200));
            return response;
        }

        catch (Exception e){
            ResponseEntity<String> response = new ResponseEntity<String>("Pokemon Not Found",HttpStatusCode.valueOf(404));
            return response;
        }

        //return response;

    }
    
    // @PostMapping(path = "/search")
    // public String searchPokemon(@RequestBody MultiValueMap <String,String> form){
    //     String pokemonName = form.getFirst("pokemonname").toLowerCase();
    //     ResponseEntity<String> result =  pokemonService.getPokemon(pokemonName);
    //     return result.getBody();

    // }
}
