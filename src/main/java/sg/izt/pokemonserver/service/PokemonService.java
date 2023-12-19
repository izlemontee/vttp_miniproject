package sg.izt.pokemonserver.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.servlet.http.HttpSession;
import sg.izt.pokemonserver.Utils;
import sg.izt.pokemonserver.model.Pokemon;
import sg.izt.pokemonserver.model.PokemonType;

@Service
public class PokemonService {

    @Autowired
    csvService csvSvc;
    
    final String BASE_API_URL = "https://pokeapi.co/api/v2/";
    final String SEARCH_POKEMON = "pokemon/";
    RestTemplate restTemplate = new RestTemplate();

    public List<Pokemon> generateList(Integer teamSize){
        List<Pokemon> blankList = new ArrayList<Pokemon>();
        for(int i=0; i<teamSize; i++){
            blankList.add(new Pokemon());
        }
        return blankList;
    }


    // used in the preliminary stage, to check if the pokemon exists in the system
    public void checkPokemonExists(String pokemonName)throws Exception{
        String url_pokemonSearch = BASE_API_URL+SEARCH_POKEMON+pokemonName;
        ResponseEntity<String> result = restTemplate.getForEntity(url_pokemonSearch, String.class);
        // prints 200 if ok, 404 if not
        if(!result.getStatusCode().equals(HttpStatusCode.valueOf(200))){
            String errormessage = "Pokemon not found";
            throw new Exception(errormessage);
        }
        //return result;

    }

    public List<String> clearTeam(HttpSession session){
        List<String> pokemonList = new ArrayList<>();
        session.setAttribute(Utils.LIST_BUILDER, pokemonList);
        return pokemonList;
    }

    // get the full pokemon info for rendering
    public Pokemon getPokemonInfo(String pokemonName){
        String url_pokemonSearch = BASE_API_URL+SEARCH_POKEMON+pokemonName;
        ResponseEntity<String> result = restTemplate.getForEntity(url_pokemonSearch, String.class);
        // entire json data
        String resultBody = result.getBody().toString();
        JsonReader jReader = Json.createReader(new StringReader(resultBody));
        JsonObject jsonObject = jReader.readObject();
        
        // get the sprite url
        JsonObject spriteObj = jsonObject.getJsonObject("sprites");
        String spriteUrl = spriteObj.getString("front_default");
        
        // get the types
        JsonArray typeData = jsonObject.getJsonArray("types");
        JsonObject typeOneObj = typeData.getJsonObject(0);
        JsonObject typeOnejson = typeOneObj.getJsonObject("type");
        String typeOne = typeOnejson.getString("name");
        String typeTwo = "N/A";
        if(typeData.size()>1){
            JsonObject typeTwoObj = typeData.getJsonObject(1);
            JsonObject typeTwojson = typeTwoObj.getJsonObject("type");
            typeTwo = typeTwojson.getString("name");
        }

        Pokemon pokemon = new Pokemon(pokemonName, typeOne, typeTwo, spriteUrl);
        return pokemon;
    }


    // processes the requestbody which contains the pokemon team
    public String[] processTeamString(String teamString){
        teamString = teamString.substring(1, teamString.length()-1);
        System.out.println(teamString);
        String[] teamArray = teamString.split(",");
        for(String s:teamArray){
            s=s.trim();
        }
        return teamArray;
    }

    public List<Float> calculateTeam(List<Pokemon> pokemonList){
        Map<String,PokemonType> typeMap = csvSvc.getTypeMap();
        //System.out.println("Type map: "+typeMap);
        System.out.println("Pokemon list: "+pokemonList);
        System.out.println("List size: "+pokemonList.size());
        //csvService
        List<Float> pokemonType = new ArrayList<Float>();
        System.out.println("length: "+typeMap.get("fire").getTypings().length);
        for(Pokemon pokemon:pokemonList){
            //Pokemon pokemon = pokemonList.get(j);
            System.out.println("Reached here");
            List<Float> individualPokemonTypeTotal = new ArrayList<Float>();
            for(int i = 0; i<typeMap.get("fire").getTypings().length; i++){
                System.out.println("Count "+i);
                Float type1 = typeMap.get(pokemon.getType1()).getTypings()[i];
                Float type2 = typeMap.get(pokemon.getType2()).getTypings()[i];
                Float totalType = type1 * type2;
                individualPokemonTypeTotal.add(totalType);
                //System.out.println(individualPokemonTypeTotal.get(i));
            }
            System.out.println("individualPokemonTypeTotal: "+individualPokemonTypeTotal.size());
            for(int i = 0; i< individualPokemonTypeTotal.size(); i++){
                System.out.println("reach here");
                if(pokemonType.size() < individualPokemonTypeTotal.size()){
                    System.out.println("null type");
                    pokemonType.add(individualPokemonTypeTotal.get(i));
                }
                else{
                    System.out.println("not null type");
                    pokemonType.set(i, (pokemonType.get(i)) + (individualPokemonTypeTotal.get(i)));
                }
            }
        }
        return pokemonType;
    }



}
