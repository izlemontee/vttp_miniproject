package sg.izt.pokemonserver.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class saveTeam {

    @NotBlank(message = "Name cannot be blank.")
    @NotEmpty(message = "Please enter a team name.")
    private String teamName;

    public saveTeam(){

    }

    public saveTeam(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    
}
