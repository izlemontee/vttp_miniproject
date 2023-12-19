package sg.izt.pokemonserver.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TeamInitialise {

    @NotNull(message = "You need to enter a number!")
    @Min(value= 1, message = "Minimum 1 Pokemon!")
    @Max(value = 6, message = "Max 6 Pokemon!")
    private Integer numberOfPokemon;

    public Integer getNumberOfPokemon() {
        return numberOfPokemon;
    }

    public void setNumberOfPokemon(Integer numberOfPokemon) {
        this.numberOfPokemon = numberOfPokemon;
    }
    
}
