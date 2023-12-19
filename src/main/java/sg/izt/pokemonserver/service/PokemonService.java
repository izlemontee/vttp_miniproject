package sg.izt.pokemonserver.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.servlet.http.HttpSession;
import sg.izt.pokemonserver.Utils;
import sg.izt.pokemonserver.model.Pokemon;
import sg.izt.pokemonserver.model.PokemonType;
import sg.izt.pokemonserver.model.saveTeam;
import sg.izt.pokemonserver.model.teamPreview;
import sg.izt.pokemonserver.repo.PokemonRepo;

@Service
public class PokemonService {

    @Autowired
    csvService csvSvc;

    @Autowired
    PokemonRepo pokemonRepo;
    
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

    // clears the whole team when the user wants to start all over
    public List<String> clearTeam(HttpSession session){
        List<String> pokemonList = new ArrayList<>();
        session.setAttribute(Utils.LIST_BUILDER, pokemonList);
        return pokemonList;
    }

    // get the full pokemon info for rendering into the model (take from API)
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

    // does the calculation for type effectiveness
    public List<Float> calculateTeam(List<Pokemon> pokemonList){
        Map<String,PokemonType> typeMap = csvSvc.getTypeMap();
        //System.out.println("Type map: "+typeMap);
        // System.out.println("Pokemon list: "+pokemonList);
        // System.out.println("List size: "+pokemonList.size());
        List<Float> pokemonType = new ArrayList<Float>();
        //System.out.println("length: "+typeMap.get("fire").getTypings().length);
        for(Pokemon pokemon:pokemonList){
            //Pokemon pokemon = pokemonList.get(j);
            //System.out.println("Reached here");
            List<Float> individualPokemonTypeTotal = new ArrayList<Float>();
            for(int i = 0; i<typeMap.get("fire").getTypings().length; i++){
                //System.out.println("Count "+i);
                Float type1 = typeMap.get(pokemon.getType1()).getTypings()[i];
                Float type2 = typeMap.get(pokemon.getType2()).getTypings()[i];
                Float totalType = type1 * type2;
                individualPokemonTypeTotal.add(totalType);
                //System.out.println(individualPokemonTypeTotal.get(i));
            }
            //System.out.println("individualPokemonTypeTotal: "+individualPokemonTypeTotal.size());
            for(int i = 0; i< individualPokemonTypeTotal.size(); i++){
                if(pokemonType.size() < individualPokemonTypeTotal.size()){
                    pokemonType.add(individualPokemonTypeTotal.get(i));
                }
                else{
                    pokemonType.set(i, (pokemonType.get(i)) + (individualPokemonTypeTotal.get(i)));
                }
            }
        }
        return pokemonType;
    }

    // converts all the team info into json to save into redis
    public void convertTeamToJson(saveTeam teamInform, List<Pokemon> pokemonTeam
    ,List<Float> calculations){
        PokemonType pokemonType = new PokemonType();
        String[] typeListString = pokemonType.getTypingString();
        JsonObjectBuilder JOB = Json.createObjectBuilder();
        JsonArrayBuilder JAB = Json.createArrayBuilder();
        String teamName = teamInform.getTeamName();
        
        // for the team
        for(Pokemon pokemon:pokemonTeam){
            JsonObject pokemonJson = JOB.add("name", pokemon.getFullName())
                .add("type1",pokemon.getType1())
                .add("type2",pokemon.getType2())
                .add("spriteurl",pokemon.getSpriteUrl())
                .build();
            JAB.add(pokemonJson);
        }
        JsonArray teamJsonArray = JAB.build();

        // for the typing
        for(int i = 0; i<typeListString.length; i++){
            String type = typeListString[i];
            Float typeCaclulation = calculations.get(i);
            JOB.add(type,typeCaclulation);
        }
        JsonObject typeCalculationJson = JOB.build();

        JsonObject teamJson = JOB.add("team",teamName)
                                .add("Pokemon",teamJsonArray)
                                .add("teamcalculation",typeCalculationJson)
                                .build();
        String teamJsonString = teamJson.toString();
        pokemonRepo.SaveTeamToRedis(teamName, teamJsonString);

    }

    // to display all the teams in the database
    public List<teamPreview> displayTeams(){
        Map<Object,Object> teamMap = pokemonRepo.getAllTeams();
        Set<Object> keys = teamMap.keySet();
        List<teamPreview> teamPreviewList = new ArrayList<>();
        for(Object o:keys){
            String name = o.toString();
            String jsonString = teamMap.get(name).toString();
            String teamString = generateTeamPreview(jsonString);
            teamPreview tp = new teamPreview(name, teamString);
            teamPreviewList.add(tp);
        }
        return teamPreviewList;
    }

    //checks if the team already exists in the database
    public Boolean keyExists(String name){
        Set<Object> keys = pokemonRepo.getKeys();
        return (keys.contains(name));
    }
    
    // generates a preview of the team in the overview page of all the teams
    public String generateTeamPreview(String teamString){
        JsonReader jReader = Json.createReader(new StringReader(teamString));
        JsonObject teamJsonObject = jReader.readObject();
        List<String> nameList = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        JsonArray teamArray = teamJsonObject.getJsonArray("Pokemon");
        for(int i = 0; i<teamArray.size(); i++){
            String pokemonName = teamArray.getJsonObject(i).get("name").toString();
            pokemonName = pokemonName.replaceAll("\"", "");
            sb.append(pokemonName);
            if(i < teamArray.size()-1){
                sb.append(", ");
            }
        }
        String namelistString  = sb.toString();
        return namelistString;

    }

    // gets the team from redis
    public Map<String,Object> getIndividualTeam(String id){
        Map<String, Object> jsonAsMap = new HashMap<>();
        List<Pokemon> pokemonList = new ArrayList<>();
        String teamJson = pokemonRepo.getTeam(id);
        System.out.println(teamJson);
        JsonReader jr = Json.createReader(new StringReader(teamJson));
        JsonObject teamJsonObject = jr.readObject();
        String name = teamJsonObject.get("team").toString().replaceAll("\"", "");
        JsonArray pokemonArray = teamJsonObject.getJsonArray("Pokemon");
        for(int i = 0; i<pokemonArray.size(); i++){
            JsonObject pokemonJson = pokemonArray.getJsonObject(i);
            String pokemonName = pokemonJson.getString("name");
            String type1 = pokemonJson.getString("type1");
            String type2 = pokemonJson.getString("type2");
            String spriteurl = pokemonJson.getString("spriteurl");
            Pokemon pokemon = new Pokemon(pokemonName, type1, type2, spriteurl);
            pokemonList.add(pokemon);
        }

        JsonObject teamCalculation = teamJsonObject.getJsonObject("teamcalculation");
        System.out.println("teamcalculation");
        PokemonType type = getTeamTypeValues(teamCalculation);
        System.out.println("Endpoint");
        jsonAsMap.put("name", name);
        jsonAsMap.put("pokemon",pokemonList);
        jsonAsMap.put("types",type);

        return jsonAsMap;


    }

    public PokemonType getTeamTypeValues(JsonObject teamCalculation){
        float FIRE = Float.parseFloat(teamCalculation.get("fire").toString());
        System.out.println("fire");
        float WATER = Float.parseFloat(teamCalculation.get("water").toString());
          System.out.println("water");
        float GRASS = Float.parseFloat(teamCalculation.get("grass").toString());
          System.out.println("grass");
        float ELECTRIC= Float.parseFloat(teamCalculation.get("electric").toString());
          System.out.println("electric");
        float NORMAL = Float.parseFloat(teamCalculation.get("normal").toString());
          System.out.println("normal");
        float FLYING = Float.parseFloat(teamCalculation.get("flying").toString());
          System.out.println("flying");
        float ROCK = Float.parseFloat(teamCalculation.get("rock").toString());
          System.out.println("rock");
        float STEEL = Float.parseFloat(teamCalculation.get("steel").toString());
          System.out.println("steel");
        float BUG = Float.parseFloat(teamCalculation.get("bug").toString());
          System.out.println("bug");
        float POISON = Float.parseFloat(teamCalculation.get("poison").toString());
          System.out.println("poison");
        float PSYCHIC = Float.parseFloat(teamCalculation.get("psychic").toString());
          System.out.println("psychic");
        float GHOST = Float.parseFloat(teamCalculation.get("ghost").toString());
          System.out.println("ghost");
        float FIGHTING = Float.parseFloat(teamCalculation.get("fighting").toString());
          System.out.println("fighting");
        float DARK = Float.parseFloat(teamCalculation.get("dark").toString());
          System.out.println("dark");
        float DRAGON = Float.parseFloat(teamCalculation.get("dragon").toString());
          System.out.println("dragon");
        float FAIRY = Float.parseFloat(teamCalculation.get("fairy").toString());
          System.out.println("fairy");
        float GROUND = Float.parseFloat(teamCalculation.get("ground").toString());
          System.out.println("ground");
        float ICE = Float.parseFloat(teamCalculation.get("ice").toString());
          System.out.println("ice");

        PokemonType type = new PokemonType(FIRE, WATER, GRASS, ELECTRIC, NORMAL, FLYING, 
        ROCK, STEEL, BUG, POISON, PSYCHIC, GHOST, 
        FIGHTING, DARK, DRAGON, FAIRY, GROUND, ICE, "teamtype");

        return type;

    }

}
