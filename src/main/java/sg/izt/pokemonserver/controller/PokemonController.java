package sg.izt.pokemonserver.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.izt.pokemonserver.Utils;
import sg.izt.pokemonserver.model.Pokemon;
import sg.izt.pokemonserver.model.PokemonInForm;
import sg.izt.pokemonserver.model.PokemonType;
import sg.izt.pokemonserver.model.TeamInitialise;
import sg.izt.pokemonserver.model.saveTeam;
import sg.izt.pokemonserver.model.teamPreview;
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
    
    // initial stage of the team building process, checks for how many pokemon in the team
    @GetMapping(path = "/teamsize")
    public String getTeamSize(Model model){

        model.addAttribute("TeamInitialise", new TeamInitialise());

        return "teamsize";
    }

    // validates the team builder initialisation.
    // if valid, move on to the team builder
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


    // continue with the team builder and validates the pokemon entered
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

    // clears the whole team and starts over
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

    // displays all the pokemon in the team
    @PostMapping(path = "/displayall")
    public String displayTeam(@RequestBody MultiValueMap<String,Object> mvm, Model model, HttpSession session){
        
        String teamList = mvm.getFirst("pokemondisplay").toString();
        String[] teamListArray = pokemonSvc.processTeamString(teamList);
        List<Pokemon> pokemonList = new ArrayList<>();

        for(String s:teamListArray){
            Pokemon pokemon = pokemonSvc.getPokemonInfo(s);
            pokemonList.add(pokemon);
        }
        List<Float> typeList = pokemonSvc.calculateTeam(pokemonList);
        // pokemonList refers to the list list of pokemon objects
        // typeList is the final team calculation, also a list
        session.setAttribute("pokemonTeam", pokemonList);
        session.setAttribute("typeList", typeList);

        model.addAttribute("pokemonTeam", pokemonList);
        model.addAttribute("typeList", typeList);
        model.addAttribute("saveTeam", new saveTeam());
        return "teamdisplay";

    }

    // saves the team into database, and shows all teams
    @PostMapping(path = "/saveteam")
    public String saveCustomTeam(@Valid @ModelAttribute("saveTeam") saveTeam team, 
    BindingResult result,
    HttpSession session, Model model){
        List<Pokemon> pokemonList = (List<Pokemon>)session.getAttribute("pokemonTeam");
        List<Float> typeList = (List<Float>)session.getAttribute("typeList");
        if(result.hasErrors()){
            System.out.println("form has errors");

            model.addAttribute("pokemonTeam", pokemonList);
            model.addAttribute("typeList", typeList);
            return "teamdisplay";
        }

        if(pokemonSvc.keyExists(team.getTeamName())){

            model.addAttribute("pokemonTeam", pokemonList);
            model.addAttribute("typeList", typeList);
            model.addAttribute("keyExists",true);
            System.out.println("team in list");
            return "teamdisplay";
            

        }
        // send the team and type calculations over to the service to convert into json
        pokemonSvc.convertTeamToJson(team, pokemonList, typeList);

        // get all the teams to display in the next page
        List<teamPreview> teamPreviewList = pokemonSvc.displayTeams();
        model.addAttribute("allTeams", teamPreviewList);
        session.invalidate();
        return "allteams";
    }

    @GetMapping(path = "/teamdisplay/{id}")
    public String displayIndividualTeam(@PathVariable("id") String id, Model model){
        Map<String,Object> teamAsMap = pokemonSvc.getIndividualTeam(id);
        
        String teamName = teamAsMap.get("name").toString();
        List<Pokemon> pokemonList = (List<Pokemon>)teamAsMap.get("pokemon");
        PokemonType type = (PokemonType)teamAsMap.get("types");

        model.addAttribute("name",teamName);
        model.addAttribute("pokemonlist", pokemonList);
        model.addAttribute("typeList", type);
        
        
        return "individualteam";
    }

    // goes back to the start
    @GetMapping(path = "/goback")
    public String goBackToStart(HttpSession session){
        session.invalidate();
        return "redirect:/pokemon/teamsize";
    }



}

