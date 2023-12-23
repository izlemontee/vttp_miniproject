package sg.izt.pokemonserver.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.izt.pokemonserver.model.PokemonInGame;
import sg.izt.pokemonserver.model.Score;
import sg.izt.pokemonserver.repo.PokemonRepo;


@Controller
public class GameService {
    final String BASE_API_URL = "https://pokeapi.co/api/v2/";
    final String SEARCH_POKEMON = "pokemon/";
    final Integer LIMIT = 1025;
    final Integer numberOfOptions = 4;
    RestTemplate restTemplate = new RestTemplate();
    Random random = new Random();

    @Autowired
    PokemonRepo pokemonRepo;

    public PokemonInGame getPokemon(Integer pokemonNumber) throws Exception{
        String url_pokemonSearch = BASE_API_URL+SEARCH_POKEMON+pokemonNumber;
        ResponseEntity<String> result = restTemplate.getForEntity(url_pokemonSearch, String.class);
        if(!result.getStatusCode().equals(HttpStatusCode.valueOf(200))){
            String errormessage = "Pokemon not found";
            throw new Exception(errormessage);
        }
        // entire json data
        String resultBody = result.getBody().toString();
        JsonReader jReader = Json.createReader(new StringReader(resultBody));
        JsonObject jsonObject = jReader.readObject();
        
        // get the sprite url
        JsonObject spriteObj = jsonObject.getJsonObject("sprites");
        String spriteUrl = spriteObj.getString("front_default");
        String name = jsonObject.getJsonObject("species").getString("name");

        PokemonInGame pokemon = new PokemonInGame(name, spriteUrl);

        return pokemon;
    }

    public Map<String,PokemonInGame> constructOptions(){
        Map<String,PokemonInGame> pokemonMap = new HashMap<String,PokemonInGame>();
        //PokemonInGame pokemonInGame = getPokemon(random.nextInt(LIMIT));
        
        while(pokemonMap.keySet().size() < numberOfOptions){
            try{
                PokemonInGame pokemonInGame = getPokemon(random.nextInt(LIMIT));
                if(pokemonMap.containsKey(pokemonInGame.getName())){
                    continue;
                }
                pokemonMap.put(pokemonInGame.getName(),pokemonInGame);
            }
            catch (Exception e){
              
            }
        }
        return pokemonMap;
    }

    public PokemonInGame randomlySelectPokemon(Map<String,PokemonInGame> map){
        Integer index = random.nextInt(numberOfOptions);
        Set<String> keyset = map.keySet();
        //System.out.println("set: "+ keyset);
        String[] keyArray = keyset.toArray(new String[keyset.size()]);
        String name = keyArray[index];
        PokemonInGame pokemon = map.get(name);
        return pokemon;
    }

    public List<PokemonInGame> generateList(Map<String,PokemonInGame> map){
        List<PokemonInGame> pokemonList = new ArrayList<>();
        Set<String> keyset = map.keySet();
        for(String s:keyset){
            pokemonList.add(map.get(s));
        }
        return pokemonList;
    }

    public void saveHighScore(String name, Integer score){
        String playerInfo = name+ ":" + score.toString();
        pokemonRepo.saveHighScore(playerInfo);

    }
    
    public List<Score> getScores(){
        Long size = pokemonRepo.getSizeOfScores();
        List<Object> scoresRaw = pokemonRepo.getScores(size);
        List<String[]> scoresArrayList = new ArrayList<String[]>();
        List<Score> scoresFinal = new ArrayList<Score>();
        for(Object o:scoresRaw){
            String scoreString = o.toString();
            String[] scoreSplit = scoreString.split(":");
            Integer scoreInt = Integer.parseInt(scoreSplit[1]);
            Score score = new Score(scoreSplit[0], scoreInt);
            scoresFinal.add(score);
        }
        // for(Score s: scoresFinal){
        //     System.out.println(s.getScore());
        // }

        Comparator<Score> comparator = Comparator.comparing(highscore -> highscore.getScore());
   
        //comparator = comparator.thenComparing(comparator.reversed());
        scoresFinal = scoresFinal.stream()
                                .sorted(comparator.reversed())
                                .collect(Collectors.toList());

        return scoresFinal;
    }

    
}
