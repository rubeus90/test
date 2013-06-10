
/**
 * Enumeration class Map - write a description of the enum class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public enum Map{
    DOUBLEVAI("DoubleVai", "Une map en double V", "1V1","doublevai",32,18,2, false),
    CHAMPS("Champs","Une map tres petite ou les combats sont intenses !","2V2 - 4","champs",12,12,4,false), 
    HACHEMAP("HacheMap","Il ne faut pas etre une pelle pour gagner","1V1","hacheMap",20,20,2,false), 
    JONATHAN("Jonathan","Map by Jonathan","2V2 - 4","jonathan",48,27,4,false),
    MAPTEST("MapTest","Map de Test","","mapTest",32,18,2,false),
    MAPTEST4("MapTest4","Map de Test","","mapTest4",32,18,4,false),
    PARALLAXE("Parallaxe","Une map de taille moyenne","1V1","Parallaxe",40,15,2,false),
    SELETON("Seleton","Une grande map","3V1","seleton",30,30,4,false),
    NIVEAU1("Mission 1","La carte de la premiere mission de la campagne", "1V1","niveau1",25,12,2,true),
    COULOIR("Couloir","Une map tout en long","1V1","couloir",30,13,2,false),
    GAGNER("Gagner","Une Map de flemmarde","1V","gagner",10,10,2,false);
    
    private String aNom;
    private String aDescription;
    private String aConseil;
    private int aNbrJoueur;
    private int aLongueur;
    private int aLargeur;
    private String aFichier;
    private boolean aLock;
    
    /**
     * Constructeur des Portees d'attaque
     */
    Map(final String pNom,final String pDescription,final String pConseil, final String pFichier,final int pLongueur,final int pLargeur,final int pNbrJoueur,final boolean pLock){
        aNom = pNom;
        aDescription = pDescription;
        aConseil = pConseil;
        aFichier = pFichier;
        aNbrJoueur = pNbrJoueur;
        aLongueur=pLongueur;
        aLargeur=pLargeur;
        aLock=pLock;
    }
    
    /**
     * Accesseur pour l'attribut aNom
     * @return aNom
     */
    public String getNom(){
        return aNom;
    }
    
    /**
     * Accesseur pour l'attribut aPorteeMax
     * @return aPorteeMax
     */
    public String getDescription(){
        return aDescription;
    }
    
    /**
     * Accesseur pour l'attribut aPorteeMin
     * @return aPorteeMin
     */
    public String getConseil(){
        return aConseil;
    }
    
    /**
     * Accesseur pour l'attribut aDescription
     * @return aDescription
     */
    public int getNbrJoueur(){
        return aNbrJoueur;
    }
    
    /**
     * Accesseur pour l'attribut aDescription
     * @return aDescription
     */
    public int getLongueur(){
        return aLongueur;
    }
    
    /**
     * Accesseur pour l'attribut aDescription
     * @return aDescription
     */
    public int getLargeur(){
        return aLargeur;
    }
    
    /**
     * Accesseur pour l'attribut aDescription
     * @return aDescription
     */
    public String getFichier(){
        return aFichier;
    }
    
    /**
     * Accesseur pour l'attribut aDescription
     * @return aDescription
     */
    public boolean isVerrouille(){
        return aLock;
    }
    
    
}