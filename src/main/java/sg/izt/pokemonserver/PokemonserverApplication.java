package sg.izt.pokemonserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sg.izt.pokemonserver.service.csvService;

@SpringBootApplication
public class PokemonserverApplication implements CommandLineRunner{

	@Autowired
	csvService csvService;

	public static void main(String[] args) {
		SpringApplication.run(PokemonserverApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		csvService.readCSV();
		System.out.println("Typing CSV prepared.");
	}

}
