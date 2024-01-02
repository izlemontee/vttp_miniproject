package sg.izt.pokemonserver;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import sg.izt.pokemonserver.model.Pokemon;
// import sg.izt.pokemonserver.model.Pokemon;
// import sg.izt.pokemonserver.model.PokemonInForm;
import sg.izt.pokemonserver.model.PokemonInBuilder;

public class Utils {
    
    public static final String REDIS = "redis";

    public static final String TEAM_SIZE = "teamSize";
    public static final String COUNTER = "counter";
    public static final String LIST_BUILDER = "pokemonlist";

    // this particular list is for the initial stage when collecting the pokemon you want in your team
    // public static List<String> getPokemonListBuilder(HttpSession session){
    //     Object pokemonListObject = session.getAttribute(LIST_BUILDER);
    //     if(null == pokemonListObject){
    //         List<String> pokemonList = new ArrayList<String>();
    //         System.out.println("null list");
    //         return pokemonList;
    //     }
    //     else{
    //         System.out.println("List not null");
    //         return (List<String>)pokemonListObject;
    //     }
    // }

    public static List<Pokemon> getPokemonListBuilder(HttpSession session){
        Object pokemonListObject = session.getAttribute(LIST_BUILDER);
        if(null == pokemonListObject){
            List<Pokemon> pokemonList = new ArrayList<Pokemon>();
            System.out.println("null list");
            return pokemonList;
        }
        else{
            System.out.println("List not null");
            return (List<Pokemon>)pokemonListObject;
        }
    }

    public static void setPokemonListBuilder(HttpSession session, List<Pokemon> list){
        session.setAttribute(LIST_BUILDER, list);
    }
    
}
