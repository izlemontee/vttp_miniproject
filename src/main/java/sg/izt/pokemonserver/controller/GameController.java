package sg.izt.pokemonserver.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import sg.izt.pokemonserver.model.HardModePokemon;
import sg.izt.pokemonserver.model.PokemonInGame;
import sg.izt.pokemonserver.model.PokemonType;
import sg.izt.pokemonserver.model.Score;
import sg.izt.pokemonserver.service.GameService;

@Controller
@RequestMapping(path = "/game")
public class GameController {

    @Autowired
    GameService gameSvc;

    PokemonType ptForReference = new PokemonType();

    @GetMapping(path = "/start")
    public String showFirstPage(HttpSession session, Model model){
        session.invalidate();

        return "gamestart";
    }
    
    @GetMapping(path = "/whosthatpokemon")
    public String startGame(HttpSession session, Model model) throws Exception{

        List<Integer> newNumberList = gameSvc.generateNewListOfNumbers();

        Object difficultyObject = session.getAttribute("difficulty");
        if(difficultyObject == null){
            return "redirect:/game/start";
        }
        String difficulty = difficultyObject.toString();
        Integer lives = Integer.parseInt(session.getAttribute("lives").toString());

        // get the correct pokemon to display
        PokemonInGame correctPokemon = gameSvc.getCorrectPokemon(newNumberList);
        newNumberList = gameSvc.updateNumberList(newNumberList, correctPokemon.getDexID());
        session.setAttribute("numberList", newNumberList);
        List<PokemonInGame> options = gameSvc.generateOptions(correctPokemon);

        // Map<String,PokemonInGame> map = gameSvc.constructOptions();
        // PokemonInGame correctPokemon = gameSvc.randomlySelectPokemon(map);
        // List<PokemonInGame> pokemonGameList = gameSvc.generateList(map);

        Integer score = 0;
        session.setAttribute("score", score);
        session.setAttribute("correctPokemon", correctPokemon.getName());

        model.addAttribute("difficulty", difficulty);
        model.addAttribute("lives", lives);
        model.addAttribute("correctPokemon", correctPokemon);
        model.addAttribute("pokemonGameList", options);
        model.addAttribute("score", score);


        return "game";
    }


    @GetMapping(path = "/whosthatpokemon/arceusmode")
    public String startGameOnGodMode(HttpSession session, Model model) throws Exception{

        List<Integer> newNumberList = gameSvc.generateNewListOfNumbers();

        Object difficultyObject = session.getAttribute("difficulty");
        if(difficultyObject == null){
            return "redirect:/game/start";
        }
        String difficulty = difficultyObject.toString();
        Integer lives = Integer.parseInt(session.getAttribute("lives").toString());

        // get the correct pokemon to display
        PokemonInGame correctPokemon = gameSvc.getCorrectPokemonOnArceusMode(newNumberList);
        newNumberList = gameSvc.updateNumberList(newNumberList, correctPokemon.getDexID());
        session.setAttribute("numberList", newNumberList);

        // Map<String,PokemonInGame> map = gameSvc.constructOptions();
        // PokemonInGame correctPokemon = gameSvc.randomlySelectPokemon(map);
        // List<PokemonInGame> pokemonGameList = gameSvc.generateList(map);

        Integer score = 0;
        session.setAttribute("score", score);
        session.setAttribute("correctPokemon", correctPokemon);

        

        model.addAttribute("difficulty", difficulty);
        model.addAttribute("lives", lives);
        model.addAttribute("correctPokemon", correctPokemon);
        model.addAttribute("score", score);
        model.addAttribute("hardmodepokemon",new HardModePokemon());
        model.addAttribute("typingstring",ptForReference.getTypingStringWithNA());


        return "gamearceusmode";
    }


    @PostMapping(path = "/whosthatpokemon/arceusmode")
    // public String runGameOnArceusMode(@RequestBody MultiValueMap<String,String> mvm,

    //  HttpSession session, Model model) throws Exception{
    public String runGameOnArceusMode(@ModelAttribute (name = "hardmodepokemon") HardModePokemon submission,
     HttpSession session, Model model) throws Exception{


        // String submissionName = mvm.getFirst("pokemonselection");
        // String submissiontype1 = mvm.getFirst("type1");
        // String submissiontype2 = mvm.getFirst("type2");
        // HardModePokemon submission = new HardModePokemon(submissionName, submissiontype1, submissiontype2);

        Object difficultyObject = session.getAttribute("difficulty");
        String difficulty = difficultyObject.toString();
        Integer lives = Integer.parseInt(session.getAttribute("lives").toString());
        PokemonInGame correct = (PokemonInGame)session.getAttribute("correctPokemon");
        Integer score = (Integer)session.getAttribute("score");

        if(lives <=0){
            model.addAttribute("correctPokemon",session.getAttribute("correctPokemon"));
            model.addAttribute("score", score);
            return "gamearceusmodeend";
        }

        if(gameSvc.compareResults(correct, submission)){
            score += 1;
            session.setAttribute("score", score);

            // TODO: handle exception
            List<Integer> numberList = (List<Integer>)session.getAttribute("numberList");

            PokemonInGame correctPokemon = gameSvc.getCorrectPokemonOnArceusMode(numberList);
            numberList = gameSvc.updateNumberList(numberList, correctPokemon.getDexID());
            session.setAttribute("numberList", numberList);
            
            // the new one
            session.setAttribute("correctPokemon", correctPokemon);


            model.addAttribute("difficulty", difficulty);
            model.addAttribute("lives", lives);
            model.addAttribute("correctPokemon", correctPokemon);
            model.addAttribute("score", score);
            model.addAttribute("hardmodepokemon",new HardModePokemon());
            model.addAttribute("typingstring",ptForReference.getTypingStringWithNA());

            return "gamearceusmode";
        }

        lives -= 1;
        session.setAttribute("lives", lives);
        model.addAttribute("correctPokemon",session.getAttribute("correctPokemon"));
        model.addAttribute("score", score);
        
        
        return "gamearceusmodeend";
    }

    @PostMapping(path = "/whosthatpokemon")
    public String runGame(@RequestBody MultiValueMap<String,Object> mvm, HttpSession session, Model model)throws Exception{
        
        Object difficultyObject = session.getAttribute("difficulty");
        String difficulty = difficultyObject.toString();
        Integer lives = Integer.parseInt(session.getAttribute("lives").toString());



        String selection = mvm.getFirst("pokemonselection").toString().toLowerCase();
        String correct = session.getAttribute("correctPokemon").toString();
        Integer score = (Integer)session.getAttribute("score");

        if(lives <=0){
            model.addAttribute("correctPokemon",session.getAttribute("correctPokemon"));
            model.addAttribute("score", score);
            return "gameend";
        }

        if(selection.equals(correct)){

            score +=1;
            session.setAttribute("score", score);
            if(score == 1018){
                model.addAttribute("score", score);
                return "gamecomplete";
            }

            // Map<String,PokemonInGame> map = gameSvc.constructOptions();
            // PokemonInGame correctPokemon = gameSvc.randomlySelectPokemon(map);
            // List<PokemonInGame> pokemonGameList = gameSvc.generateList(map);

            List<Integer> numberList = (List<Integer>)session.getAttribute("numberList");
            System.out.println("list size: "+numberList.size());
            // get the correct pokemon to display
            PokemonInGame correctPokemon = gameSvc.getCorrectPokemon(numberList);
            numberList = gameSvc.updateNumberList(numberList, correctPokemon.getDexID());
            session.setAttribute("numberList", numberList);
            List<PokemonInGame> options = gameSvc.generateOptions(correctPokemon);



            // the new one
            session.setAttribute("correctPokemon", correctPokemon.getName());

            model.addAttribute("difficulty", difficulty);
            model.addAttribute("lives", lives);
            model.addAttribute("correctPokemon", correctPokemon);
            model.addAttribute("pokemonGameList", options);
            model.addAttribute("score", score);

            return "game";
        }

        else{
            lives -= 1;
            session.setAttribute("lives", lives);
            if(lives > 0){

                // Map<String,PokemonInGame> map = gameSvc.constructOptions();
                // PokemonInGame correctPokemon = gameSvc.randomlySelectPokemon(map);
                // List<PokemonInGame> pokemonGameList = gameSvc.generateList(map);

                List<Integer> numberList = (List<Integer>)session.getAttribute("numberList");
                // get the correct pokemon to display
                PokemonInGame correctPokemon = gameSvc.getCorrectPokemon(numberList);
                numberList = gameSvc.updateNumberList(numberList, correctPokemon.getDexID());
                session.setAttribute("numberList", numberList);
                List<PokemonInGame> options = gameSvc.generateOptions(correctPokemon);
                
                session.setAttribute("correctPokemon", correctPokemon.getName());

                model.addAttribute("difficulty", difficulty);
                model.addAttribute("lives", lives);
                model.addAttribute("correctPokemon", correctPokemon);
                model.addAttribute("pokemonGameList", options);
                model.addAttribute("score", score);

                return "game";

            }
            
            model.addAttribute("correctPokemon",session.getAttribute("correctPokemon"));
            model.addAttribute("score", score);
            return "gameend";
        }
    }

    @PostMapping(path="/save")
    public String saveGame(@RequestBody MultiValueMap<String,Object> mvm, HttpSession session){
        String difficulty = session.getAttribute("difficulty").toString();
        String name = mvm.getFirst("player").toString();
        System.out.println("reached here");
        Integer score = (Integer)session.getAttribute("score");
        gameSvc.saveHighScore(name,score, difficulty);
        session.invalidate();
        return "redirect:/game/highscore";
    }

    @GetMapping(path="/highscore")
    public String showHighScore(Model model){

        List<Score> scoresList = gameSvc.getScores();
        model.addAttribute("scores",scoresList);
        return "gamescores";

    }

    @GetMapping(path="/easy")
    public String easyMode(HttpSession session){
        session.setAttribute("difficulty", "easy");
        session.setAttribute("lives", 7);
        return "redirect:/game/whosthatpokemon";
    }
    @GetMapping(path="/medium")
    public String mediumMode(HttpSession session){
        session.setAttribute("difficulty", "medium");
        session.setAttribute("lives", 3);
        return "redirect:/game/whosthatpokemon";
    }

    @GetMapping(path="/hard")
    public String hardMode(HttpSession session){
        session.setAttribute("difficulty", "hard");
        session.setAttribute("lives", 1);
        return "redirect:/game/whosthatpokemon";
    }

    @GetMapping(path = "/master")
    public String masterMode(HttpSession session){
        session.setAttribute("difficulty", "master");
        session.setAttribute("lives", 1);
        return "redirect:/game/whosthatpokemon";
    }

    @GetMapping(path = "/arceus")
    public String arceusMode(HttpSession session){
        session.setAttribute("difficulty", "arceus");
        session.setAttribute("lives", 1);
        return "redirect:/game/whosthatpokemon/arceusmode";
    }
}
