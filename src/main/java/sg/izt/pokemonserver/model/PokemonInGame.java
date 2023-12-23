package sg.izt.pokemonserver.model;

public class PokemonInGame {

    private String name;
    private String url;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public PokemonInGame(String name, String url) {
        this.name = name;
        this.url = url;
    }
    
}