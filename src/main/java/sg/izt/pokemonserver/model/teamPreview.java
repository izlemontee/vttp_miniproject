package sg.izt.pokemonserver.model;

public class teamPreview {
    
    private String name;
    private String team;

    public teamPreview(String name, String team) {
        this.name = name;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public teamPreview(){
        
    }
}
