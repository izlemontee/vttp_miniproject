package sg.izt.pokemonserver.model;

public class Pokemon {
    private String fullName;
    private String type1;
    private String type2;
    private String spriteUrl;

    public Pokemon(String fullName, String type1, String type2, String spriteUrl) {
        this.fullName = fullName;
        this.type1 = type1;
        this.type2 = type2;
        this.spriteUrl = spriteUrl;
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
    public String getSpriteUrl() {
        return spriteUrl;
    }
    public void setSpriteUrl(String spriteUrl) {
        this.spriteUrl = spriteUrl;
    }
    public Pokemon(){

    }
    public Pokemon(String fullName){
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


}
