package sg.izt.pokemonserver.repo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.izt.pokemonserver.Utils;

@Repository
public class PokemonRepo {
    @Autowired @Qualifier(Utils.REDIS)//ask spring to look for something, look for myredis bean
	private RedisTemplate<String,Object> template;

    public void SaveTeamToRedis(String teamname, String teamJsonString){
        template.opsForHash().put("teamcalculator", teamname, teamJsonString);
        System.out.println("Success");

    }

    public Map<Object,Object> getAllTeams(){
        Map<Object,Object> allTeams = template.opsForHash().entries("teamcalculator");
        return allTeams;
    }

    public Set<Object> getKeys(){
        Set<Object> keys = template.opsForHash().keys("teamcalculator");
        return keys;
    }
    
    public String getTeam(String id){
        return template.opsForHash().get("teamcalculator",id).toString();
    }

    public void saveHighScore(String playerInfo, String difficulty){
        ListOperations<String,Object> LO = template.opsForList();
        LO.leftPush("game",playerInfo);
    }

    public void getScoresPerDifficulty(String difficulty){
        ListOperations<String,Object> LO = template.opsForList();
        

    }

    public Long getSizeOfScores(){
        ListOperations<String,Object> LO = template.opsForList();
        Long size = LO.size("game");
        return size;
    }

    public List<Object> getScores(Long size){
        ListOperations<String,Object> LO = template.opsForList();
        List<Object> scores = LO.range("game",0,size);

        return scores;
    }
}
