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
    PokemonService pokemonSvc;

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
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        for(String s:typings){
            JOB.add(s,(JsonNumber)Json.createValue(Double.parseDouble(teamCalculation.get(s).toString())));
        }
        JsonObject types = JOB.build();
        return types;
    }

    public List<Pokemon> generateTeam(String[] teamArray){
        List<Pokemon> teamList = new ArrayList<Pokemon>();
        for (String s: teamArray){
            Pokemon pokemon = pokemonSvc.getPokemonInfo(s);
            teamList.add(pokemon);
        }
        return teamList;

    }

    public Map<String,Object> generateTeamFromScratch(List<Pokemon> team, List<Float> calculation){
        Map<String,Object> completeMap = new HashMap<>();
        List<Map> teamList = new ArrayList<>();
        PokemonType pt = new PokemonType();
        String[] typeStrings = pt.getTypingString();
        for(Pokemon p : team){
            Map<String,Object> individualPokemonMap = new HashMap<>();
            individualPokemonMap.put("name", p.getFullName());
            individualPokemonMap.put("type1", p.getType1());
            individualPokemonMap.put("type2", p.getType2());
            individualPokemonMap.put("spriteurl",p.getSpriteUrl());

            Map<String,Float> typeMap = listTypeEffectivenessFromScratch(p.getIndividualPokemonTypeTotal(), typeStrings);
            individualPokemonMap.put("typeeffectiveness",typeMap);
            teamList.add(individualPokemonMap);
        }

        Map<String,Float> teamTypeMap = listTypeEffectivenessFromScratch(calculation, typeStrings);

        completeMap.put("Pokemon",teamList);
        completeMap.put("teamcalculation",teamTypeMap);



        return completeMap;
    }

    public Map<String,Float> listTypeEffectivenessFromScratch(List<Float> typeCalculation, String[] typings){
        Map<String, Float> typeMap = new HashMap<>();
        for(int i = 0; i<typings.length; i++){
            Float type = typeCalculation.get(i);
            typeMap.put(typings[i],type);
        }
        return typeMap;
    }

}
