package sg.izt.pokemonserver.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
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
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.izt.pokemonserver.model.HardModePokemon;
//import sg.izt.pokemonserver.model.Pokemon;
import sg.izt.pokemonserver.model.PokemonInGame;
import sg.izt.pokemonserver.model.Score;
import sg.izt.pokemonserver.repo.PokemonRepo;


@Controller
public class GameService {
    final String BASE_API_URL = "https://pokeapi.co/api/v2/";
    final String SEARCH_POKEMON = "pokemon/";
    final Integer LIMIT = 1017;
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
        //System.out.println(resultBody);
        JsonReader jReader = Json.createReader(new StringReader(resultBody));
        JsonObject jsonObject = jReader.readObject();
        
        // get the sprite url
        JsonObject spriteObj = jsonObject.getJsonObject("sprites");
        //System.out.println(spriteObj);
        // String spriteUrl = spriteObj.get("front_default").toString();
        String spriteUrl = spriteObj.getString("front_default").toString();
        String name = jsonObject.getJsonObject("species").getString("name");
        Integer dexId = jsonObject.getJsonNumber("id").intValue();
        PokemonInGame pokemon = new PokemonInGame(name, spriteUrl,dexId);

        return pokemon;
    }


    
    public PokemonInGame getPokemonOnArceusMode(Integer pokemonNumber) throws Exception{
        String url_pokemonSearch = BASE_API_URL+SEARCH_POKEMON+pokemonNumber;
        ResponseEntity<String> result = restTemplate.getForEntity(url_pokemonSearch, String.class);
        if(!result.getStatusCode().equals(HttpStatusCode.valueOf(200))){
            String errormessage = "Pokemon not found";
            throw new Exception(errormessage);
        }
        
        // entire json data
        String resultBody = result.getBody().toString();
        //System.out.println(resultBody);
        JsonReader jReader = Json.createReader(new StringReader(resultBody));
        JsonObject jsonObject = jReader.readObject();
        
        // get the sprite url
        JsonObject spriteObj = jsonObject.getJsonObject("sprites");
        //System.out.println(spriteObj);
        // String spriteUrl = spriteObj.get("front_default").toString();
        String spriteUrl = spriteObj.getString("front_default").toString();
        String name = jsonObject.getJsonObject("species").getString("name");
        Integer dexId = jsonObject.getJsonNumber("id").intValue();
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
        PokemonInGame pokemon = new PokemonInGame(name, spriteUrl,dexId,typeOne,typeTwo);

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

    public void saveHighScore(String name, Integer scoreInt, String difficulty){
        String playerInfo = name+ "," + scoreInt.toString() + "," + difficulty;
        Integer difficultyInt = 0;
        switch(difficulty){
                    case "easy":
                    difficultyInt = 1;
                    break;

                    case "medium":
                    difficultyInt = 2;
                    break;

                    case "hard":
                    difficultyInt = 3;
                    break;

                    case "master":
                    difficultyInt = 4;
                    break;

                    case "arceus":
                    difficultyInt = 5;
                    break;
                }
        Score score = new Score(name, scoreInt, difficulty, difficultyInt);
        List<Score> sortedScores = sortScoresPerDifficulty(difficulty,score);
        pokemonRepo.deleteList(difficulty);
        for(Score s: sortedScores){
            String scoreInfo = s.getName()+ "," + s.getScore().toString() + "," + difficulty;
            pokemonRepo.saveHighScore(scoreInfo,difficulty);
        }
    }

    public Map<String,List<Score>> getAllScores(){
        String easy = "easy";
        String medium = "medium";
        String hard = "hard";
        String master = "master";
        String arceus = "arceus";
        
        List<Score> easyScores = retrieveScoresByDifficulty(easy);
        List<Score> mediumScores = retrieveScoresByDifficulty(medium);
        List<Score> hardScores = retrieveScoresByDifficulty(hard);
        List<Score> masterScores = retrieveScoresByDifficulty(master);
        List<Score> arceusScores = retrieveScoresByDifficulty(arceus);

        Map<String,List<Score>> scoresList = new HashMap<>();
        scoresList.put(easy,easyScores);
        scoresList.put(medium,mediumScores);
        scoresList.put(hard,hardScores);
        scoresList.put(master,masterScores);
        scoresList.put(arceus,arceusScores);

        return scoresList;
    }

    public List<Score> retrieveScoresByDifficulty(String difficulty){
        List<Object> scoresRaw = pokemonRepo.getScoresRaw(difficulty);
        List<Score> scoresPlaceholder = new ArrayList<Score>();
        List<Score> scoresFinal = new ArrayList<Score>();
        Integer maxIndex = 10;
        if(scoresRaw == null){
        }
        else{
            for(Object o:scoresRaw){
                //scores.add(o.toString());
                String scoreString = o.toString();
                String[] scoreSplit = scoreString.split(",");
                Integer scoreInt = Integer.parseInt(scoreSplit[1]);
                String difficultyString = scoreSplit[2];
                Integer difficultyInt = 0;
                switch(difficulty){
                    case "easy":
                    difficultyInt = 1;
                    break;

                    case "medium":
                    difficultyInt = 2;
                    break;

                    case "hard":
                    difficultyInt = 3;
                    break;

                    case "master":
                    difficultyInt = 4;
                    break;

                    case "arceus":
                    difficultyInt = 5;
                    break;
                }
                Score score = new Score(scoreSplit[0], scoreInt, difficulty, difficultyInt);
                scoresPlaceholder.add(score);
                }
            Comparator<Score> comparator = Comparator.comparing(highscore -> highscore.getScore());
            scoresPlaceholder = scoresPlaceholder.stream()
                        .sorted(comparator.reversed())
                        .collect(Collectors.toList());
            if(scoresPlaceholder.size()<maxIndex){
                return scoresPlaceholder;
            }
            else{
                for(int i=0; i<maxIndex; i++){
                    scoresFinal.add(scoresPlaceholder.get(i));
                }
            }

        }
        return scoresFinal;

    }

    public List<Score> sortScoresPerDifficulty(String difficulty, Score scoreToSave){
        List<Object> scoresRaw = pokemonRepo.getScoresRaw(difficulty);
        List<Score> scoresPlaceholder = new ArrayList<Score>();
        List<Score> scoresFinal = new ArrayList<Score>();
        Integer maxIndex = 10;
        if(scoresRaw == null){
        }
        else{
            for(Object o:scoresRaw){
                //scores.add(o.toString());
                String scoreString = o.toString();
                String[] scoreSplit = scoreString.split(",");
                Integer scoreInt = Integer.parseInt(scoreSplit[1]);
                String difficultyString = scoreSplit[2];
                Integer difficultyInt = 0;
                switch(difficulty){
                    case "easy":
                    difficultyInt = 1;
                    break;

                    case "medium":
                    difficultyInt = 2;
                    break;

                    case "hard":
                    difficultyInt = 3;
                    break;

                    case "master":
                    difficultyInt = 4;
                    break;

                    case "arceus":
                    difficultyInt = 5;
                    break;
                }
                Score score = new Score(scoreSplit[0], scoreInt, difficulty, difficultyInt);
                scoresPlaceholder.add(score);
                }
            scoresPlaceholder.add(scoreToSave);
            Comparator<Score> comparator = Comparator.comparing(highscore -> highscore.getScore());
            scoresPlaceholder = scoresPlaceholder.stream()
                        .sorted(comparator.reversed())
                        .collect(Collectors.toList());
            if(scoresPlaceholder.size()<maxIndex){
                return scoresPlaceholder;
            }
            else{
                for(int i=0; i<maxIndex; i++){
                    scoresFinal.add(scoresPlaceholder.get(i));
                }
            }

        }
        return scoresFinal;
    }
    
    public List<Score> getScores(){
        Long size = pokemonRepo.getSizeOfScores();
        List<Object> scoresRaw = pokemonRepo.getScores(size);
        List<String[]> scoresArrayList = new ArrayList<String[]>();
        List<Score> scoresFinal = new ArrayList<Score>();
        for(Object o:scoresRaw){
            String scoreString = o.toString();
            String[] scoreSplit = scoreString.split(",");
            Integer scoreInt = Integer.parseInt(scoreSplit[1]);
            String difficulty = scoreSplit[2];
            Integer difficultyInt = 0;
            switch(difficulty){
                case "easy":
                difficultyInt = 1;
                break;

                case "medium":
                difficultyInt = 2;
                break;

                case "hard":
                difficultyInt = 3;
                break;

                case "master":
                difficultyInt = 4;
                break;

                case "arceus":
                difficultyInt = 5;
                break;
            }
            Score score = new Score(scoreSplit[0], scoreInt, difficulty, difficultyInt);
            scoresFinal.add(score);
        }
        // for(Score s: scoresFinal){
        //     System.out.println(s.getScore());
        // }

        Comparator<Score> comparator = Comparator.comparing(highscore -> highscore.getDifficultyInt());
        comparator = comparator.thenComparing(Comparator.comparing(highscore -> highscore.getScore()));
        // Comparator<Score> comparator = Comparator.comparing(highscore -> highscore.getScore());
        // comparator = comparator.thenComparing(Comparator.comparing(highscore -> highscore.getDifficultyInt()));
   
        //comparator = comparator.thenComparing(comparator.reversed());
        scoresFinal = scoresFinal.stream()
                                .sorted(comparator.reversed())
                                .collect(Collectors.toList());

        return scoresFinal;
    }


    // generates a fresh list of numbers to choose from for the api
    public List<Integer> generateNewListOfNumbers(){
        List<Integer> numberList = new ArrayList<Integer>();
        for(Integer i = 1; i<1018;i++){
            numberList.add(i);
        }

        return numberList;
    }


    // for the correct pokemon to be shown
    //public PokemonInGame getCorrectPokemon(List<Integer> numberList) throws Exception{
    public PokemonInGame getCorrectPokemon(List<Integer> numberList) throws Exception{
        Integer index = random.nextInt(numberList.size());
        System.out.println("RNG: "+index);
        System.out.println("Ndex number: "+numberList.get(index));
        Integer numberForApi = numberList.get(index);
        PokemonInGame pokemonInGame = null;
        while(pokemonInGame == null){
            try{
                pokemonInGame = getPokemon(numberForApi);
            }
            catch (Exception e){
                System.out.println("API error");
            }
        }
        //pokemonInGame = getPokemon(numberForApi);
        return pokemonInGame;
    }


    
    // for the correct pokemon to be shown
    //public PokemonInGame getCorrectPokemon(List<Integer> numberList) throws Exception{
    public PokemonInGame getCorrectPokemonOnArceusMode(List<Integer> numberList) throws Exception{
        Integer index = random.nextInt(numberList.size());
        System.out.println("RNG: "+index);
        System.out.println("Ndex number: "+numberList.get(index));
        Integer numberForApi = numberList.get(index);
        PokemonInGame pokemonInGame = null;
        while(pokemonInGame == null){
            try{
                pokemonInGame = getPokemonOnArceusMode(numberForApi);
            }
            catch (Exception e){
                System.out.println("API error");
            }
        }
        //pokemonInGame = getPokemon(numberForApi);
        return pokemonInGame;
    }

    // update the number list 
    public List<Integer> updateNumberList(List<Integer> numberList, Integer number){
        Integer index = numberList.indexOf(number);
        System.out.println("The pokemon number was: "+number);
        System.out.println("the index to be remove: "+index);
        
        numberList.remove(index);

        return numberList;
    }

    // generate the options
    //public List<PokemonInGame> generateOptions(PokemonInGame correctPokemon)throws Exception{
    public List<PokemonInGame> generateOptions(PokemonInGame correctPokemon)throws Exception{
        List<PokemonInGame> options = new ArrayList<PokemonInGame>();
        while(options.size()<3){
            try{
                PokemonInGame pokemonInGame = getPokemon(random.nextInt(LIMIT));
                options.add(pokemonInGame);
            }
            catch(Exception e){
                System.out.println("API error");
            }
        }
        // for(int i = 0; i<3;i++){
        //     PokemonInGame pokemonInGame = getPokemon(random.nextInt(LIMIT));
        //     System.out.println("can");
        //     options.add(pokemonInGame);
        // }
        options.add(correctPokemon);
        Collections.shuffle(options);
        return options;

    }

    public boolean compareResults(PokemonInGame correct, HardModePokemon submission){
        if(submission.getName().equals(correct.getName())){
            boolean compareTypeOneTypeOne = submission.getType1().equals(correct.getType1());
            boolean compareTypeOneTypeTwo = submission.getType1().equals(correct.getType2());
            if(compareTypeOneTypeOne || compareTypeOneTypeTwo){
                boolean compareTypeTwoTypeOne = submission.getType2().equals(correct.getType1());
                boolean compareTypeTwoTypeTwo = submission.getType2().equals(correct.getType2());
                boolean typeOneXORTypeTwo = !(submission.getType1().equals(submission.getType2()));
                if((compareTypeTwoTypeOne || compareTypeTwoTypeTwo) && typeOneXORTypeTwo){
                    return true;
                }
            }
        }

        return false;

    }

    
}
