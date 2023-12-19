package sg.izt.pokemonserver.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import sg.izt.pokemonserver.csvColumns;
import sg.izt.pokemonserver.model.PokemonType;

@Service
public class csvService {
    Map<String,PokemonType> typeMap;

    public void readCSV() throws Exception{
        String fileName = "static/pokemontypes.csv";
        Resource resource = new ClassPathResource(fileName);
        try(InputStream is = resource.getInputStream()){
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            // read the first line
            String firstLine = br.readLine();
            typeMap = br.lines()
                .map(line -> line.split(","))
                .map(line ->
                    new PokemonType(
                        Float.parseFloat(line[csvColumns.FIRE_COL]), 
                        Float.parseFloat(line[csvColumns.WATER_COL]), 
                        Float.parseFloat(line[csvColumns.GRASS_COL]), 
                        Float.parseFloat(line[csvColumns.ELECTRIC_COL]), 
                        Float.parseFloat(line[csvColumns.NORMAL_COL]),
                        Float.parseFloat(line[csvColumns.FLYING_COL]),
                        Float.parseFloat(line[csvColumns.ROCK_COL]),
                        Float.parseFloat(line[csvColumns.STEEL_COL]),
                        Float.parseFloat(line[csvColumns.BUG_COL]), 
                        Float.parseFloat(line[csvColumns.POISON_COL]), 
                        Float.parseFloat(line[csvColumns.PSYCHIC_COL]), 
                        Float.parseFloat(line[csvColumns.GHOST_COL]), 
                        Float.parseFloat(line[csvColumns.FIGHTING_COL]), 
                        Float.parseFloat(line[csvColumns.DARK_COL]), 
                        Float.parseFloat(line[csvColumns.DRAGON_COL]), 
                        Float.parseFloat(line[csvColumns.FAIRY_COL]), 
                        Float.parseFloat(line[csvColumns.GROUND_COL]), 
                        Float.parseFloat(line[csvColumns.ICE_COL]), 
                        line[csvColumns.NAME_COL])
                )
                .collect(Collectors.toMap(line-> line.getTYPE(), line-> line));
            typeMap.put("N/A", new PokemonType(
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        1f,
                        1f,
                        1f,
                        1f,
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        1f, 
                        "N/A")
            );
            // for(int i=0; i<typeMap.get("fire").getTypings().length;i++){
            //     System.out.println(typeMap.get("fire").getTypings()[i]);
            // }
        }
    } 

    public Map<String,PokemonType> getTypeMap(){
        return typeMap;
    }
}
