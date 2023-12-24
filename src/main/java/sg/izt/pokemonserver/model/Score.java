package sg.izt.pokemonserver.model;

public class Score {

    private String name;
    private Integer score;
    private String difficulty;
    private Integer difficultyInt;
    
    public Integer getDifficultyInt() {
        return difficultyInt;
    }
    public void setDifficultyInt(Integer difficultyInt) {
        this.difficultyInt = difficultyInt;
    }
    public Score(String name, Integer score, String difficulty, Integer difficultyInt) {
        this.name = name;
        this.score = score;
        this.difficulty = difficulty;
        this.difficultyInt = difficultyInt;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    public Score(String name, Integer score, String difficulty) {
        this.name = name;
        this.score = score;
        this.difficulty = difficulty;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public Score(String name, Integer score) {
        this.name = name;
        this.score = score;
    }
    
}
