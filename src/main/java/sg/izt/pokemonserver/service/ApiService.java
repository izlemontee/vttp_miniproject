package sg.izt.pokemonserver.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import sg.izt.pokemonserver.model.Pokemon;
import sg.izt.pokemonserver.model.PokemonType;
import sg.izt.pokemonserver.repo.PokemonRepo;

@Service
public class ApiService {
    @Autowired
    csvService csvSvc;

    @Autowired
    PokemonRepo pokemonRepo;
    
    public Map<String,Object> getTeamAsMap(String id){
        PokemonType pt = new PokemonType();
        String[] typeStrings = pt.getTypingString();
        String teamJson = pokemonRepo.getTeam(id);
        JsonObjectBuilder mainJOB = Json.createObjectBuilder();
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        JsonReader jr = Json.createReader(new StringReader(teamJson));
        JsonObject teamJsonObject = jr.readObject();
        Map<String, Object> teamAsMap = new HashMap<>();
        List<Map> pokemonList = new ArrayList<>();
        // team name
        String name = teamJsonObject.get("team").toString().replaceAll("\"", "");

        // pokemon array
        JsonArray pokemonArray = teamJsonObject.getJsonArray("Pokemon");
        for(int i = 0; i<pokemonArray.size(); i++){
            JsonObjectBuilder JOB = Json.createObjectBuilder();
            Map<String, Object> pokemonAsMap = new HashMap<String,Object>();

            JsonObject pokemonJson = pokemonArray.getJsonObject(i);
            String pokemonName = pokemonJson.getString("name");
            String type1 = pokemonJson.getString("type1");
            String type2 = pokemonJson.getString("type2");
            String spriteurl = pokemonJson.getString("spriteurl");
            


            JsonString pokemonNameJsonString = Json.createValue(pokemonName);
            JOB.add("name",(JsonString)Json.createValue(pokemonName))
                .add("type1",(JsonString)Json.createValue(type1))
                .add("type2",(JsonString)Json.createValue(type2))
                .add("spriteurl",(JsonString)Json.createValue(spriteurl));

            JsonObject individualTypeEffectivenessJson = pokemonJson.getJsonObject("typeeffectiveness");
            //individualTypeEffectivenessJson = parseTypeEffectiveness(individualTypeEffectivenessJson, typeStrings);
            JOB.add("typeeffectiveness",individualTypeEffectivenessJson);
            JsonObject pokemonJsonObj = JOB.build();
            JAB.add(pokemonJsonObj);


            Map<String,Float> typeMap = listTypeEffectiveness(individualTypeEffectivenessJson, typeStrings);
            pokemonAsMap.put("typeeffectiveness",typeMap);
            pokemonAsMap.put("spriteurl",spriteurl);
            pokemonAsMap.put("type2",type2);
            pokemonAsMap.put("type1",type1);
            pokemonAsMap.put("name",pokemonName);
            pokemonList.add(pokemonAsMap);
        }
        JsonArray pokemonJsonArray = JAB.build();

        // team calculations
        JsonObject teamCalculation = teamJsonObject.getJsonObject("teamcalculation");
        teamCalculation = parseTypeEffectiveness(teamCalculation, typeStrings);

        Map<String,Float> teamTypeMap = listTypeEffectiveness(teamCalculation, typeStrings);


        JsonObject jsonResponse = mainJOB.add("team",(JsonString)Json.createValue(name))
            .add("Pokemon",pokemonJsonArray)
            .add("teamcalculation",teamCalculation)
            .build();

        teamAsMap.put("team",name);
        
        teamAsMap.put("Pokemon",pokemonList);
        teamAsMap.put("teamcalculation",teamTypeMap);
   

        return teamAsMap;


    }
    public Map<String,Float> listTypeEffectiveness(JsonObject teamCalculation, String[] typings){
        Map<String, Float> typeMap = new HashMap<>();
        for(String s:typings){
            typeMap.put(s,Float.parseFloat(teamCalculation.get(s).toString()));
        }
        return typeMap;
    }

    public JsonObject parseTypeEffectiveness(JsonObject teamCalculation, String[] typings){
        // float FIRE = Float.parseFloat(teamCalculation.get("fire").toString());
        // float WATER = Float.parseFloat(teamCalculation.get("water").toString());
        // float GRASS = Float.parseFloat(teamCalculation.get("grass").toString());
        // float ELECTRIC= Float.parseFloat(teamCalculation.get("electric").toString());
        // float NORMAL = Float.parseFloat(teamCalculation.get("normal").toString());
        // float FLYING = Float.parseFloat(teamCalculation.get("flying").toString());
        // float ROCK = Float.parseFloat(teamCalculation.get("rock").toString());
        // float STEEL = Float.parseFloat(teamCalculation.get("steel").toString());
        // float BUG = Float.parseFloat(teamCalculation.get("bug").toString());
        // float POISON = Float.parseFloat(teamCalculation.get("poison").toString());
        // float PSYCHIC = Float.parseFloat(teamCalculation.get("psychic").toString());
        // float GHOST = Float.parseFloat(teamCalculation.get("ghost").toString());
        // float FIGHTING = Float.parseFloat(teamCalculation.get("fighting").toString());
        // float DARK = Float.parseFloat(teamCalculation.get("dark").toString());
        // float DRAGON = Float.parseFloat(teamCalculation.get("dragon").toString());
        // float FAIRY = Float.parseFloat(teamCalculation.get("fairy").toString());
        // float GROUND = Float.parseFloat(teamCalculation.get("ground").toString());
        // float ICE = Float.parseFloat(teamCalculation.get("ice").toString());

        JsonObjectBuilder JOB = Json.createObjectBuilder();
        for(String s:typings){
            JOB.add(s,(JsonNumber)Json.createValue(Double.parseDouble(teamCalculation.get(s).toString())));
        }
        JsonObject types = JOB.build();
        return types;
    }

    public Map<String, Object> testMap(){
        Map<String, Object> testMap = new HashMap<String, Object>();
        Map<String, String> nestedMap = new HashMap<String, String>();
        List<String> list = new ArrayList<>();
        list.add("listone");
        list.add("listtwo");
        list.add("listthree");
        nestedMap.put("one","one");
        nestedMap.put("keytwo","two");
        testMap.put("Name","name");
        testMap.put("nested",nestedMap);
        testMap.put("list",list);
        return testMap;
    }

}
