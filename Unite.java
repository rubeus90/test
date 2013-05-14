
/****************************************
 * 
 * CLASSE UNITE
 * Gere les caractéritistiques des unitées 
 * 
 * @author Thibault
 * @version 1.0
 * 
 */
public class Unite
{
    // instance variables - replace the example below with your own
    private String aType; // Correspond au type d'unité : Infanterie, Véhicule etc ...
    private int aAttaque; // Correspond à l'attaque au corps à corps
    private int aVie; //Correspond aux nombres de points de vie de l'unite
    private int aDeplacement; // Coresspond au déplacement maximum que peut effectuer l'unité
    private int aLvl; // Correspond au niveau de l'unité
    private int aExperience; // Correspond à l'expérience total de l'unité
    private int aGain; //Compris entre 1 et 2, correspondant au pourcentage d'augmentation des caractéristique à chaque monté de niveau

    /**
     * Constructeur de la classe Unite
     * Prend en paramètre :
     * un string correspondant au type d'unité
     * Un int correspondant au dégat au corps à corps
     * Un int correspondant au déplacement maximum
     */
    public Unite(final String pType,final int pAttaque,final int pVie,final int pDeplacement, final int pGain)
    {
       aType = pType;
       aAttaque = pAttaque;
       aDeplacement = pDeplacement;
       aGain = pGain;
       aVie = pVie;
       aLvl = 1;
       aExperience = 0;
    }

    
    /**
     * Accesseur qui renvoi la valeur de l'attaque au corps à corps
     * @return aAttaque
     */
    public int getAttaque(){
        return aAttaque;
    }
    
    /**
     * Accesseur qui renvoi la valeur du nombre de point de vie de l'unite
     * @return aVie
     */
    public int getVie(){
        return aVie;
    }
    
    /**
     * Accesseur qui renvoi l'expérience total de l'unite
     * @return aExperience
     */
    public int getExperience(){
        return aExperience;
    }
    
    /**
     * Accesseur qui renvoi le niveau de l'unite
     * @return aLvl
     */
    public int getLvl(){
        return aLvl;
    }
    
    /**
     * Accesseur qui renvoi le deplacement maximum de l'unite
     * @return aDeplacement
     */
    public int getDeplacement(){
        return aDeplacement;
    }
    
     /**
     * Modificateur de l'Experience
     * Permet lorsque l'unite gagne un combat, d'augmenter son experience
     * @param pExperience
     */
    public void setExperience(final int pExperience){
        aExperience+=pExeperience;
        // Une fois que l'unite gagne de l'experience, on regarde si elle peut monter de niveau
        upLvl();
    }
    
    /*******
     * Modificateur de l attaque de l'unite
     * Permet lorsque l'unite monte de niveau d'augmenter son attaque
     * @param pAttaque
     */
    public void setVie(final int pVie){
        aVie+=pVie;
    }
    
    
    /*******
     * Modificateur de l attaque de l'unite
     * Permet lorsque l'unite monte de niveau d'augmenter son attaque
     * @param pAttaque
     */
    private void setAttaque(final int pAttaque){
        aAttaque+=pAttaque
    }
    
    /*******
     * Modificateur du deplacement de l'unites
     * Permet lorsque l'unite monte de niveau d'augmenter son deplacement
     * @param pDeplacement
     */
    private void setDeplacement(final int pDeplacement){
        aDeplacement+=pDeplacement;
    }
    
    /*******
     * Methode qui permet a une unite de monter de niveau
     * On part du principe que l'unite monte de niveau tous les 1000xp
     */
    private void upLvl(){
        vExperience = (aLvl) * 1000; // On calcul l'experience a avoir pour le niveau suivant
        if(aExperience-vExperience>1){ // Si l'experience de l'unite est superieur au l'experience pour le niveau suivant, on monte le niveau de l'unite et ses caractéristique
            aLvl++;
            aAttaque*=aGain;
            aDeplacement*=aGain;
            aVie*=aGain;
        }
    }
       
}

