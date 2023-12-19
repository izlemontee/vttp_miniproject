package sg.izt.pokemonserver.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.izt.pokemonserver.Utils;
import sg.izt.pokemonserver.model.Pokemon;
import sg.izt.pokemonserver.model.PokemonInForm;
import sg.izt.pokemonserver.model.TeamInitialise;
import sg.izt.pokemonserver.service.PokemonService;


//@Validated
@Controller
@RequestMapping(path = "/pokemon")
public class PokemonController {

    @Autowired
    PokemonService pokemonSvc;

    @GetMapping(path = "/search", produces = "text/html")
    public String searchPage(Model model){
        
        return "searchpokemon";
    }
    
    @GetMapping(path = "/teamsize")
    public String getTeamSize(Model model){

        model.addAttribute("TeamInitialise", new TeamInitialise());

        return "teamsize";
    }

    @PostMapping(path = "/teambuilderinit")
    public String getPokemon(@Valid @ModelAttribute("TeamInitialise") TeamInitialise form, 
    BindingResult result, Model model, HttpSession session){
        if(result.hasErrors()){
            return "teamsize";
        }
        else{
            // how many pokemon you want in your team
            Integer teamSize = form.getNumberOfPokemon();
            // sets the team size indicated in the session
            session.setAttribute(Utils.TEAM_SIZE, teamSize);
            // initialises the counter so that it counts down
            session.setAttribute(Utils.COUNTER, 0);

            // at this point it should be an empty list, size 0
            List<String> pokemonList = Utils.getPokemonListBuilder(session);
            // how many pokemon left to enter
            Integer remainingSize = teamSize - pokemonList.size();
            model.addAttribute("remainingSize", remainingSize);
            model.addAttribute("pokemonListBuilder",pokemonList);
            model.addAttribute("pokemonInForm", new PokemonInForm());
   
            return "teambuilder";
        }
    }

    @PostMapping(path = "/teambuilder")
    //public String getTeamInfo(@ModelAttribute("blanklist") List<Pokemon> list){
    public String getTeamInfo(@Valid @ModelAttribute("pokemonInForm") PokemonInForm form, BindingResult result,
    Model model,HttpSession session){
        List<String> pokemonList = Utils.getPokemonListBuilder(session);

        if(result.hasErrors()){
            Integer remainingSize = (Integer)session.getAttribute(Utils.TEAM_SIZE) - pokemonList.size();
            model.addAttribute("remainingSize", remainingSize);
            model.addAttribute("pokemonListBuilder",pokemonList);
            return "teambuilder";
        }
        else{
            // if there are no validation errors, then add to the list and keep repeating until it reaches the team size
            String fullName = form.getFullName();
            try{
            pokemonSvc.checkPokemonExists(fullName);
            pokemonList.add(fullName);
            Utils.setPokemonListBuilder(session, pokemonList);
            Integer remainingSize = (Integer)session.getAttribute(Utils.TEAM_SIZE) - pokemonList.size();
            model.addAttribute("remainingSize", remainingSize);
            //session.setAttribute(Utils.COUNTER, ((Integer)session.getAttribute(Utils.COUNTER)+1));
            if(pokemonList.size()<(Integer)session.getAttribute(Utils.TEAM_SIZE)){
                model.addAttribute("pokemonListBuilder", pokemonList);
                model.addAttribute("pokemonInForm", new PokemonInForm());
                return "teambuilder";
            }

            }
            catch(Exception e){
                result.addError(new ObjectError("pokemonAPIerror", "Pokemon not found in database"));
                if(result.hasGlobalErrors()){
                    System.out.println("global error");
                    boolean pokemonAPIerror = true;
                    Integer remainingSize = (Integer)session.getAttribute(Utils.TEAM_SIZE) - pokemonList.size();
                    model.addAttribute("remainingSize", remainingSize);
                    model.addAttribute("pokemonAPIerror", pokemonAPIerror);
                    model.addAttribute("pokemonInForm", new PokemonInForm());
                    model.addAttribute("pokemonListBuilder",pokemonList);
                return "teambuilder";
                }
            }
        
            // when all the pokemon are valid and added to the list
            model.addAttribute("pokemonListBuilder",pokemonList);
            return "finaliseteam";
        
        }


    }

    @PostMapping(path = "/remove")
    public String removePokemon(HttpSession session, Model model, 
    @RequestBody MultiValueMap <String,String> mvm){

            String pokemonRemove = mvm.getFirst("pokemonRemove");
            System.out.println(pokemonRemove);
            // sets the team size indicated in the session
            Integer teamSize = (Integer)session.getAttribute(Utils.TEAM_SIZE);
            // initialises the counter so that it counts down
            session.setAttribute(Utils.COUNTER, 0);

            // at this point it should be an empty list, size 0
            List<String> pokemonList = Utils.getPokemonListBuilder(session);
            pokemonList.remove(pokemonRemove);
            // how many pokemon left to enter
            Integer remainingSize = teamSize - pokemonList.size();
            model.addAttribute("remainingSize", remainingSize);
            model.addAttribute("pokemonListBuilder",pokemonList);
            model.addAttribute("pokemonInForm", new PokemonInForm());
   
            return "teambuilder";
        
    }

    @PostMapping(path = "/clearall")
    public String clearTeam(HttpSession session, Model model){
        List<String> pokemonList = pokemonSvc.clearTeam(session);
        Integer teamSize = (Integer)session.getAttribute(Utils.TEAM_SIZE);
        Integer remainingSize = teamSize - pokemonList.size();
        model.addAttribute("remainingSize", remainingSize);
        model.addAttribute("pokemonListBuilder",pokemonList);
        model.addAttribute("pokemonInForm", new PokemonInForm());
   
        return "teambuilder";

    }

    @PostMapping(path = "/displayall")
    public String displayTeam(@RequestBody MultiValueMap<String,Object> mvm, Model model, HttpSession session){
        session.invalidate();
        String teamList = mvm.getFirst("pokemondisplay").toString();
        String[] teamListArray = pokemonSvc.processTeamString(teamList);
        List<Pokemon> pokemonList = new ArrayList<>();

        for(String s:teamListArray){
            Pokemon pokemon = pokemonSvc.getPokemonInfo(s);
            pokemonList.add(pokemon);
        }
        List<Float> typeList = pokemonSvc.calculateTeam(pokemonList);
        model.addAttribute("pokemonTeam", pokemonList);
        model.addAttribute("typeList", typeList);
        return "teamdisplay";

    }

    @GetMapping(path = "/goback")
    public String goBackToStart(HttpSession session){
        session.invalidate();
        return "redirect:/pokemon/teamsize";
    }



}

