package sg.izt.pokemonserver.model;

import jakarta.validation.constraints.NotEmpty;

public class PokemonInForm {


    @NotEmpty(message = "You need to enter a name")
    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
}
