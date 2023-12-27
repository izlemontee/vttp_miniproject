package sg.izt.pokemonserver.model;

public class PokemonInGame {

    private String name;
    private String url;
    private Integer dexID;
    private String type1;
    private String type2;
    



    public PokemonInGame(String name, String url, Integer dexID, String type1, String type2) {
        this.name = name;
        this.url = url;
        this.dexID = dexID;
        this.type1 = type1;
        this.type2 = type2;
    }
    public String getType1() {
        return type1;
    }
    public void setType1(String type1) {
        this.type1 = type1;
    }
    public String getType2() {
        return type2;
    }
    public void setType2(String type2) {
        this.type2 = type2;
    }
    public Integer getDexID() {
        return dexID;
    }
    public void setDexID(Integer dexID) {
        this.dexID = dexID;
    }
    public PokemonInGame(String name, String url, Integer dexID) {
        this.name = name;
        this.url = url;
        this.dexID = dexID;
    }
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
