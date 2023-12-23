package sg.izt.pokemonserver.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import sg.izt.pokemonserver.model.PokemonInGame;
import sg.izt.pokemonserver.model.Score;
import sg.izt.pokemonserver.service.GameService;

@Controller
@RequestMapping(path = "/game")
public class GameController {

    @Autowired
    GameService gameSvc;

    @GetMapping(path = "/start")
    public String showFirstPage(HttpSession session, Model model){

        return "gamestart";
    }
    
    @GetMapping(path = "/whosthatpokemon")
    public String startGame(HttpSession session, Model model){
        Map<String,PokemonInGame> map = gameSvc.constructOptions();
        PokemonInGame correctPokemon = gameSvc.randomlySelectPokemon(map);
        List<PokemonInGame> pokemonGameList = gameSvc.generateList(map);

        Integer score = 0;
        session.setAttribute("score", score);
        session.setAttribute("correctPokemon", correctPokemon.getName());

        model.addAttribute("correctPokemon", correctPokemon);
        model.addAttribute("pokemonGameList", pokemonGameList);
        model.addAttribute("score", score);


        return "game";
    }

    @PostMapping(path = "/whosthatpokemon")
    public String runGame(@RequestBody MultiValueMap<String,Object> mvm, HttpSession session, Model model){
        String selection = mvm.getFirst("pokemonselection").toString();
        String correct = session.getAttribute("correctPokemon").toString();
        Integer score = (Integer)session.getAttribute("score");
        if(selection.equals(correct)){

            Map<String,PokemonInGame> map = gameSvc.constructOptions();
            PokemonInGame correctPokemon = gameSvc.randomlySelectPokemon(map);
            List<PokemonInGame> pokemonGameList = gameSvc.generateList(map);


            score +=1;
            session.setAttribute("score", score);
            // the new one
            session.setAttribute("correctPokemon", correctPokemon.getName());
            model.addAttribute("correctPokemon", correctPokemon);
            model.addAttribute("pokemonGameList", pokemonGameList);
            model.addAttribute("score", score);

            return "game";
        }

        else{

            model.addAttribute("score", score);
            return "gameend";
        }
    }

    @PostMapping(path="/save")
    public String saveGame(@RequestBody MultiValueMap<String,Object> mvm, HttpSession session){
        String name = mvm.getFirst("player").toString();
        System.out.println("reached here");
        Integer score = (Integer)session.getAttribute("score");
        gameSvc.saveHighScore(name,score);
        session.invalidate();
        return "redirect:/game/highscore";
    }

    @GetMapping(path="/highscore")
    public String showHighScore(Model model){

        List<Score> scoresList = gameSvc.getScores();
        model.addAttribute("scores",scoresList);
        return "gamescores";

    }
}
