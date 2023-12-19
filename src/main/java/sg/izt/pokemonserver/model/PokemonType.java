package sg.izt.pokemonserver.model;

public class PokemonType {

    private float FIRE;
    private float WATER;
    private float GRASS;
    private float ELECTRIC;
    private float NORMAL;
    private float FLYING;
    private float ROCK;
    private float STEEL;
    private float BUG;
    private float POISON;
    private float PSYCHIC;
    private float GHOST;
    private float FIGHTING;
    private float DARK;
    private float DRAGON;
    private float FAIRY;
    private float GROUND;
    private float ICE;

    private String TYPE;

    public PokemonType(){

    }

    public String getTYPE() {
        return TYPE;
    }
    public void setTYPE(String tYPE) {
        TYPE = tYPE;
    }
    public float getFIRE() {
        return FIRE;
    }
    public void setFIRE(float fIRE) {
        FIRE = fIRE;
    }
    public float getWATER() {
        return WATER;
    }
    public void setWATER(float wATER) {
        WATER = wATER;
    }
    public float getGRASS() {
        return GRASS;
    }
    public void setGRASS(float gRASS) {
        GRASS = gRASS;
    }
    public float getELECTRIC() {
        return ELECTRIC;
    }
    public void setELECTRIC(float eLECTRIC) {
        ELECTRIC = eLECTRIC;
    }
    public float getNORMAL() {
        return NORMAL;
    }
    public void setNORMAL(float nORMAL) {
        NORMAL = nORMAL;
    }
    public float getFLYING() {
        return FLYING;
    }
    public void setFLYING(float fLYING) {
        FLYING = fLYING;
    }
    public float getROCK() {
        return ROCK;
    }
    public void setROCK(float rOCK) {
        ROCK = rOCK;
    }
    public float getSTEEL() {
        return STEEL;
    }
    public void setSTEEL(float sTEEL) {
        STEEL = sTEEL;
    }
    public float getBUG() {
        return BUG;
    }
    public void setBUG(float bUG) {
        BUG = bUG;
    }
    public float getPOISON() {
        return POISON;
    }
    public void setPOISON(float pOISON) {
        POISON = pOISON;
    }
    public float getPSYCHIC() {
        return PSYCHIC;
    }
    public void setPSYCHIC(float pSYCHIC) {
        PSYCHIC = pSYCHIC;
    }
    public float getGHOST() {
        return GHOST;
    }
    public void setGHOST(float gHOST) {
        GHOST = gHOST;
    }
    public float getFIGHTING() {
        return FIGHTING;
    }
    public void setFIGHTING(float fIGHTING) {
        FIGHTING = fIGHTING;
    }
    public float getDARK() {
        return DARK;
    }
    public void setDARK(float dARK) {
        DARK = dARK;
    }
    public float getDRAGON() {
        return DRAGON;
    }
    public void setDRAGON(float dRAGON) {
        DRAGON = dRAGON;
    }
    public float getFAIRY() {
        return FAIRY;
    }
    public void setFAIRY(float fAIRY) {
        FAIRY = fAIRY;
    }
    public float getGROUND() {
        return GROUND;
    }
    public void setGROUND(float gROUND) {
        GROUND = gROUND;
    }
    public float getICE() {
        return ICE;
    }
    public void setICE(float iCE) {
        ICE = iCE;
    }
    public PokemonType(float fIRE, float wATER, float gRASS, float eLECTRIC, float nORMAL, float fLYING, float rOCK,
            float sTEEL, float bUG, float pOISON, float pSYCHIC, float gHOST, float fIGHTING, float dARK, float dRAGON,
            float fAIRY, float gROUND, float iCE, String TYPE) {
        FIRE = fIRE;
        WATER = wATER;
        GRASS = gRASS;
        ELECTRIC = eLECTRIC;
        NORMAL = nORMAL;
        FLYING = fLYING;
        ROCK = rOCK;
        STEEL = sTEEL;
        BUG = bUG;
        POISON = pOISON;
        PSYCHIC = pSYCHIC;
        GHOST = gHOST;
        FIGHTING = fIGHTING;
        DARK = dARK;
        DRAGON = dRAGON;
        FAIRY = fAIRY;
        GROUND = gROUND;
        ICE = iCE;
        this.TYPE = TYPE;
    }

    public Float[] getTypings(){
        Float[] typings = {
            getFIRE(),
            getWATER(),
            getGRASS(),
            getELECTRIC(),
            getNORMAL(),
            getFLYING(),
            getROCK(),
            getSTEEL(),
            getBUG(),
            getPOISON(),
            getPSYCHIC(),
            getGHOST(),
            getFIGHTING(),
            getDARK(),
            getDRAGON(),
            getFAIRY(),
            getGROUND(),
            getICE()

        };
        return typings;
    }
    public String[] getTypingString(){
        String[] typings = {"fire","water","grass","electric","normal","flying","rock","steel","bug"
        ,"poison","psychic","ghost","fighting","dark","dragon","fairy","ground","ice"};

        return typings;
        }
    

    
}
