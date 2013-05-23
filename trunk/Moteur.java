import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.awt.Point;
import java.util.PriorityQueue;

/*
 * Possede presque toutes les methodes propres a la mecanique du jeu, il va travailler de paire avec Partie et IHM
 */
class Moteur
{  
    Unite uniteD; // unite en attente de déplacement
    Unite uniteA; // unite en attente d'attaque
    boolean[][] tabAtt;
    
    int[][] tabDist;
    Point[][] pred;
    
    Point[] voisins = {new Point(0,1), new Point(0,-1),new Point(1,0),new Point(-1,0)};
    Point[] signes = {new Point(1,1), new Point(1,-1),new Point(-1,-1),new Point(-1,1), new Point(0,1), new Point(0,-1), new Point(-1,0), new Point(1,0)};
    
    
    
    public Moteur()
    {
        tabDist = new int[Slatch.partie.getLargeur()][Slatch.partie.getHauteur()];
        pred = new Point[Slatch.partie.getLargeur()][Slatch.partie.getHauteur()];
        tabAtt= new boolean[Slatch.partie.getLargeur()][Slatch.partie.getHauteur()];
       
        uniteD = null;
        uniteA = null;
    }
   
    public void enleverSurbrillance()
    {
        for(int i=0; i<Slatch.partie.getLargeur(); i++)
        {
            for(int j=0; j<Slatch.partie.getHauteur(); j++)
            {
                if(Slatch.partie.getTerrain()[i][j].getSurbrillance())
                {
                    Slatch.partie.getTerrain()[i][j].setSurbrillance(false);
                    Slatch.ihm.getPanel().repaint();
                }
            }
        }
    } 
      
    /**
     * Methode a deplacer dans Moteur Une fois moteur safe
     * Methode qui permet a un ingenieur de soigner une unite
     * @param pUnite
     */
    public void soin(Unite pUnite)
    { 
        int vPV=pUnite.getPointDeVie();
        int vPVMax = pUnite.getPVMax();
        
        if(vPV<vPVMax){ // On verifie que le nbr de PV est inferieur au nbr de PV max
            
           if(vPV+5>vPVMax){ // Si lorsqu'on soigne on depasse le nbr de PV max, alors Vie de l'unite = PVmax
               pUnite.setPointDeVie(vPVMax);
            }
           else{ //Sinon on ajoute 5
               pUnite.setPointDeVie(vPV+5);
           }
            
           //On "grise" l'unite
           uniteA.attaque(true);
           uniteA.deplacee(true);
           uniteA=null; 
        }
    }
    
    /**
     * Methode a deplacer dans Moteur Une fois moteur safe
     * Methode qui permet a un ingenieur de faire evoluer une methode
     * @param pUnite
     */
    public void evoluer(Unite pUnite){
        if(pUnite.getExperience()>Unite.pallierExperience){ //On verifie que l'XP de l'unite est superieur au pallier pour monter d'XP
            pUnite.upLvl();
            
            //Ingenieur grisee
            uniteA.attaque(true);
            uniteA.deplacee(true);
            uniteA=null;
            
            //Unite evoluer grisee
            pUnite.attaque(true);
            pUnite.deplacee(true);
            pUnite=null;
        }
    }
    
    public void modeAttaque(int pX, int pY)
    {
        uniteA = Slatch.partie.getTerrain()[pX][pY].getUnite();
        affichePorteeAttaque(uniteA);
    }
    
    public void attaque(Unite pVictime)
    { 
        double degatsAtt=0;
        //degatsAtt=(uniteA.getAttaque().getDegats()*uniteA.getAttaque().efficacite.get(pVictime.getType()))*(100-(TypeTerrain.bonusCouverture*Slatch.partie.getTerrain()[pVictime.getCoordonneeX()][pVictime.getCoordonneeY()].getType().getCouverture()))/100;
        degatsAtt = getDegats(uniteA, pVictime);
        //pVictime.setPointDeVie(pVictime.getPointDeVie() - (int)degatsAtt);
        if(faireDegats(pVictime, degatsAtt)) // si la victime meurt
        {
            uniteA.addExperience(Unite.EXPERIENCE_DONNEE_PAR_NIVEAU*pVictime.getLvl());
            estMort(pVictime);
        }    
        else if(distance(uniteA, pVictime)==1 && pVictime.getAttaque().aTypePortee.getPorteeMin()==1) //sinon + si attaque au CAC, on riposte
        {
            degatsAtt= 0.7*getDegats(pVictime, uniteA);
            if(faireDegats(uniteA, degatsAtt))
            {
                uniteA.addExperience(Unite.EXPERIENCE_DONNEE_PAR_NIVEAU*uniteA.getLvl());
                estMort(uniteA);
            }
        }
        uniteA.attaque(true);
        uniteA.deplacee(true);
        uniteA=null;
    }
    
    public void capture(int pX, int pY)
    {
        Terrain vBatiment= Slatch.partie.getTerrain()[pX][pY];
        uniteA=Slatch.partie.getTerrain()[pX][pY].getUnite();
       
       
        vBatiment.setPointDeVie(vBatiment.getPointDeVie()-10);//Bon... ça va capturer la batiment directement...Valeur à changer
        if(vBatiment.getPointDeVie()<=0)
        {
           vBatiment.setJoueur(uniteA.getJoueur());
           Slatch.ihm.getPanel().repaint();
        }
        uniteA.attaque(true);
        uniteA.deplacee(true);
        uniteA=null;
    }

    
    public boolean faireDegats(Unite cible, double degats) // retourne vrai si la cible meurt
    {
        cible.setPointDeVie(cible.getPointDeVie() - (int)degats);
        if(cible.getPointDeVie()<=0){return true;}else{Slatch.ihm.getPanel().repaint(); return false;}
    }
    
    public double getDegats(Unite a, Unite v) // a= attaquant, v= cible
    {
        return ((a.getAttaque().getDegats()*a.getAttaque().efficacite.get(v.getType()))*(100-(TypeTerrain.bonusCouverture*Slatch.partie.getTerrain()[v.getCoordonneeX()][v.getCoordonneeY()].getType().getCouverture()))/100)*((double)a.getPointDeVie()/(double)a.getPVMax());
    }
   
    public void estMort(Unite unite)
    {
        Slatch.partie.getTerrain()[unite.getCoordonneeX()][unite.getCoordonneeY()].setUnite(null);
        Slatch.ihm.getPanel().repaint();
        Slatch.partie.getJoueur(unite.getJoueur()).getListeUnite().remove(unite);
        if(Slatch.partie.getJoueur(unite.getJoueur()).getListeUnite().isEmpty())
        {
            System.out.println("Le joueur "+unite.getJoueur()+" est vaincu");
        }
    }

    /**
     * Appelee par l'IHM quand on clique sur une case, cette methode doit generer la liste des coordonnees accessibles par l'unite se trouvant sur la case selectionnee si elle ne s'est pas deja deplacee, et passer cette Liste a l'IHM.
     */
    public void caseSelectionnee(int pX, int pY)
    {
        
        if(Slatch.partie.getTerrain()[pX][pY].getSurbrillance() && uniteA==null && uniteD == null)
        {
            this.enleverSurbrillance();
            return;
        }
        
        this.enleverSurbrillance();
        
        
        if(uniteA==null && uniteD == null)
        {
            Slatch.partie.getTerrain()[pX][pY].setSurbrillance(true);
        }
        Unite unite = Slatch.partie.getTerrain()[pX][pY].getUnite();
        if(unite==null) // si aucune unité n'est présente sur la case
        {
            annulerAttaque();
            if(uniteD!=null && tabDist[pX][pY]>-1) // si on a sélectioné aucune unité auparavant pour le déplacement
            {
                deplacement(uniteD, pX, pY);
            }
            annulerDeplacement();
        }
        else
        {
            if(uniteA==null) // si on a sélectionné aucune unité auparavant pour l'attaque
            {
                if(uniteD==null)
                {   if(Slatch.partie.getJoueurActuel()==unite.getJoueur())
                    {
                        List<String> items= new ArrayList<String>();//on va afficher le menu en créant une liste d'items
                        if(!unite.dejaDeplacee()){items.add("Deplace");}
                        if(cibleEnVue(unite) && !unite.dejaAttaque()){items.add("Attaque");}
                        if((unite.getType()==TypeUnite.COMMANDO /*|| unite.getType()==TypeUnite.DEMOLISSEUR*/) && Slatch.partie.getTerrain()[pX][pY].getType()==TypeTerrain.BATIMENT && Slatch.partie.getJoueurActuel()!=Slatch.partie.getTerrain()[pX][pY].getJoueur())
                        {
                                items.add("Capture");
                        }
                        Slatch.partie.getTerrain()[pX][pY].setSurbrillance(true);
                        if(!items.isEmpty()){Slatch.ihm.getPanel().afficheMenu(items, pX, pY);}
                    }
                }
                else
                {
                    annulerDeplacement();
                }
            }
            else
            {
                if(unite.getJoueur()!=uniteA.getJoueur() && uniteA.getAttaque().efficacite.containsKey(unite.getType()) && tabAtt[pX][pY]) // si l'unité ciblée n'appartient pas au même joueur que l'attaquant, et que l'attaquant a une attaque qui peut toucher la cible, alors on attaque
                {
                        attaque(unite);
                }
                else
                {
                        annulerAttaque();
                }
            }
        }
    }
    
    public void modeDeplacement(int pX, int pY)
    {
        uniteD = Slatch.partie.getTerrain()[pX][pY].getUnite();
        affichePorteeDep(uniteD);
    }
    
    public void annulerDeplacement()
    {
        uniteD=null;
    }
    
    public void annulerAttaque()
    {
        uniteA=null;
    }
    
    /**
    * permet de déplacer une unité en fonction du chemin passé en paramètre
    * @param unite unite a deplacer
    * @param pX abscisse de l'arrivee
    * @param pY ordonnee de l'arrivee
    */
    public void deplacement(Unite unite, int pX, int pY)
    {
        boolean fini = false;
        int x = pX, y =pY;
        Stack<Point> chemin = new Stack<Point>();
        
        if(pred[x][y]!=null){chemin.push(new Point(pX,pY));}
        while(!fini)
        {
            Point p = pred[x][y];
            if(p!=null)
            {
                x=(int)p.getX();
                y=(int)p.getY();
                if(unite.getCoordonneeX()==x && unite.getCoordonneeY()==y)
                {
                    fini = true;
                    
                }
                else
                {
                    chemin.push(p);
                }
            }
            else{break;}
        }
        int k = chemin.size();
        int l = unite.getPorteeDeplacement();
        while(!chemin.isEmpty())
        {
            Point p = chemin.pop();
            if((l-=Slatch.partie.getTerrain()[(int)p.getX()][(int)p.getY()].getCout(unite))<0)
            {
                break;
            }
            changerCase(unite, (int)p.getX(), (int)p.getY());
            
            try{
                Thread.sleep(250/k+50);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        unite.deplacee(true);
    }
    
    /**
     * Deplace une unite vers sa destination
     * @param unite unite a deplacer
     * @param pX abscisse de l'arrivee
     * @param pY ordonnee de l'arrivee
     */
    public void changerCase(Unite unite, int destX, int destY)
    {
        Slatch.partie.getTerrain()[unite.getCoordonneeX()][unite.getCoordonneeY()].setUnite(null);
        unite.setCoordonneeX(destX); unite.setCoordonneeY(destY);
        Slatch.partie.getTerrain()[destX][destY].setUnite(unite);

        Slatch.ihm.getPanel().paintImmediately(0,0,Slatch.ihm.getPanel().getWidth(),Slatch.ihm.getPanel().getHeight());

    }
    
    /**
     * Vérifie si une unité se trouve à côté de la case passée en paramètre
     */
    public boolean uniteProche(Unite unite, int pX, int pY)
    {
        for(Point p: voisins)
        {
            int decX = (int)p.getX();
            int decY = (int)p.getY();
            if(pX+decX<Slatch.partie.getLargeur() && pX+decX>=0 && pY+decY<Slatch.partie.getHauteur() && pY+decY>=0)
            {
                if(Slatch.partie.getTerrain()[pX+decX][pY+decY].getUnite()!=null)
                {
                    if(Slatch.partie.getTerrain()[pX+decX][pY+decY].getUnite().getJoueur()!=unite.getJoueur())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Verifie si une unite se trouve a portee de tir de l'unite passee en parametre
     * @param unite unite qui cherche une autre unite a frapper
     * @return true si une unite est a portee de tir, false sinon
     */
    private boolean cibleEnVue(Unite unite)
    {
        int x = unite.getCoordonneeX(), y = unite.getCoordonneeY();
        for(int i=0; i<=unite.getAttaque().aTypePortee.getPorteeMax();i++)
        {
            for(int j=0; j<=unite.getAttaque().aTypePortee.getPorteeMax();j++)
            {
                for(Point p: signes)
                {
                    int decX = (int)p.getX();
                    int decY = (int)p.getY();
                    if(ciblePresente(unite, i*decX,j*decY)){return true;}
                }
            }
        }
        return false;
    }
    
    /**
     * Affiche la portee d'attaque en mettant en surbrillance les cibles potentielles
     * @param unite unite qui cherche une autre unite a frapper
     */
    public void affichePorteeAttaque(Unite unite)
    {
        int x = unite.getCoordonneeX();
        int y = unite.getCoordonneeY();

        for(int i=0; i<Slatch.partie.getLargeur(); i++)
        {
            for(int j=0; j<Slatch.partie.getHauteur(); j++)
            {
                tabAtt[i][j] = false;
            }
        }
        
        for(int i=0; i<=unite.getAttaque().aTypePortee.getPorteeMax();i++)
        {
            for(int j=0; j<=unite.getAttaque().aTypePortee.getPorteeMax();j++)
            {
                for(Point p: signes)
                {
                    int decX = (int)p.getX();
                    int decY = (int)p.getY();
                    if(ciblePresente(unite, i*decX,j*decY))
                    {
                        Slatch.partie.getTerrain()[x+i*decX][y+j*decY].setSurbrillance(true);
                        Slatch.ihm.getPanel().repaint();
                        tabAtt[x+i*decX][y+j*decY] = true;
                    }
                }
            }
        }
        
    }
    
    /**
     * vérifie si une cible est présente en (x+decX, y+decY)
     */
    private boolean ciblePresente(Unite unite, int decX, int decY)
    {
        int x = unite.getCoordonneeX();
        int y = unite.getCoordonneeY();
        
        if(x+decX<Slatch.partie.getLargeur() && y+decY<Slatch.partie.getHauteur() && x+decX>=0 && y+decY>=0)
        {
            if(distance(x+decX, y+decY, x,y)>=unite.getAttaque().aTypePortee.getPorteeMin() && distance(x+decX, y+decY, x,y)<=unite.getAttaque().aTypePortee.getPorteeMax() && distance(x+decX, y+decY, x,y)>=unite.getAttaque().aTypePortee.getPorteeMin() && Slatch.partie.getTerrain()[x+decX][y+decY].getUnite()!=null)
            {
                return (Slatch.partie.getTerrain()[x+decX][y+decY].getUnite().getJoueur()!=Slatch.partie.getJoueurActuel())&&!(unite.dejaDeplacee() && distance(x+decX, y+decY, x,y)>=2);
            }
        }
        return false;
    }
    
    /**
     * renvoie la distance entre (dX,dY) et (aX,aY)
     */
    public int distance(int dX, int dY, int aX, int aY)
    {
        return Math.abs(dX-aX) + Math.abs(dY-aY);
    }
    
    /**
     * renvoie la distance entre e1 et e2
     */
    public int distance(Entite e1, Entite e2)
    {
        return distance(e1.getCoordonneeX(), e1.getCoordonneeY(), e2.getCoordonneeX(), e2.getCoordonneeY());
    }
    
    public void remplitPorteeDep(Unite unite, boolean bool)
    {
        this.initialiseTabDist(unite.getCoordonneeX(), unite.getCoordonneeY());
        this.algoDeplacement(unite, bool);
    }
    
    /**
     * va appeler les methodes pour afficher la portee de deplacement d'une unite
     * @param unite unite qui a envie de bouger
     */
    private void affichePorteeDep(Unite unite)
    {
        this.remplitPorteeDep(unite, true);
        for(int i=0; i<Slatch.partie.getLargeur(); i++)
        {
            for(int j=0; j<Slatch.partie.getHauteur(); j++)
            {
                if(tabDist[i][j]>0)
                {
                    Slatch.partie.getTerrain()[i][j].setSurbrillance(true);
                    Slatch.ihm.getPanel().repaint();
                }
            }
        }
    }
   
    /**
     * Initialise tabDist afin d'utiliser algoDeplacement dans des conditions optimales
     */
    private void initialiseTabDist(int x, int y)
    {
        for(int i=0; i<Slatch.partie.getLargeur(); i++)
        {
            for(int j=0; j<Slatch.partie.getHauteur(); j++)
            {
                if(Slatch.partie.getTerrain()[i][j].getUnite()!=null)  // quand on a déjà une unité sur la case, on ne peut pas y accéder
                {
                    pred[i][j]=null;
                    tabDist[i][j] = -2;
                }
                else{tabDist[i][j] = -1;} // au début, on suppose qu'on a une distance infinie représentée par -1 sur chacune des cases restantes
            }
        }
        tabDist[x][y]=-2;
        pred = new Point[Slatch.partie.getLargeur()][Slatch.partie.getHauteur()];
    }
    
    private void algoDeplacement(Unite unite, boolean porteeComptee)
    {
        PriorityQueue<Triplet> pq = new PriorityQueue<Triplet>();
        pq.add(new Triplet(0,unite.getCoordonneeX(),unite.getCoordonneeY()));
        while(!pq.isEmpty())
        {
            Triplet t = pq.poll();
            for(Point p: voisins)
            {
                int x = t.x+(int)p.getX();
                int y = t.y+(int)p.getY();
                if(x>=0 && y>=0 && x<Slatch.partie.getLargeur() && y<Slatch.partie.getHauteur())
                {
                    int d = t.d+Slatch.partie.getTerrain()[x][y].getCout(unite);
                    if(d<=unite.getPorteeDeplacement() || !porteeComptee)
                    {
                        if(d<tabDist[x][y] || tabDist[x][y]==-1)
                        {
                            tabDist[x][y] = d;
                            pred[x][y] = new Point(t.x, t.y);
                            pq.offer(new Triplet(d, x, y));
                        }
                    }
                }
            }
        }
    }
    
    public void passeTour()
    {
        Slatch.partie.tourSuivant();
        List<Unite> l = Slatch.partie.getJoueur(Slatch.partie.getJoueurActuel()).getListeUnite();
        
        for(Unite u: l)
        {
            u.attaque(false);
            u.deplacee(false);
        }
        
        if(Slatch.partie.getJoueurActuel()==2){GrandeIA.test(l.get(0));} // ceci est un test et devra être remplacé rapidement par un appel à la Grande IA
    }
    
    public boolean estAuJoueurActuel(Unite unite)
    {
        return unite.getJoueur()==Slatch.partie.getJoueurActuel();
    }
    
    public boolean estAuJoueurActuel(int pX, int pY)
    {
        return estAuJoueurActuel(Slatch.partie.getTerrain()[pX][pY].getUnite());
    }
    
    public void creationUnite(final int pX,final int pY, final String pUnite){
        int vNumJoueur = Slatch.partie.getJoueurActuel();
        Joueur vJoueur = Slatch.partie.getJoueur(vNumJoueur);
        int vArgentJv = vJoueur.getArgent();
        
        switch(pUnite){
            case "commando": 
                if(vArgentJv>TypeUnite.COMMANDO.getPrix()){
                    Unite vcommando = new Unite(pX,pY,vNumJoueur,20,TypeUnite.COMMANDO,TypeAttaque.FUSIL,40,1.0, TypeDeplacement.PIED);
                    Slatch.partie.getJoueur(vNumJoueur).getListeUnite().add(vcommando);
                    Slatch.partie.getTerrain()[pX][pY].setUnite(vcommando);
                    vJoueur.setArgent(vArgentJv + TypeUnite.COMMANDO.getPrix());
                    vcommando.attaque(true);
                    vcommando.deplacee(true);
                    vcommando=null;
                }
                break;
            case "tank":
                if(vArgentJv>TypeUnite.TANK.getPrix()){
                   Unite vtank = new Unite(pX,pY,vNumJoueur,65,TypeUnite.TANK,TypeAttaque.CANON,40,1.0, TypeDeplacement.CHENILLES);
                   Slatch.partie.getJoueur(vNumJoueur).getListeUnite().add(vtank);
                   Slatch.partie.getTerrain()[pX][pY].setUnite(vtank);
                   vJoueur.setArgent(vArgentJv + TypeUnite.TANK.getPrix());
                   vtank.attaque(true);
                   vtank.deplacee(true);
                   vtank=null;
                }
                break;
            case "uml":
                if(vArgentJv>TypeUnite.UML.getPrix()){
                    Unite vuml = new Unite(pX,pY,vNumJoueur,30,TypeUnite.UML,TypeAttaque.MISSILE,50,1.0, TypeDeplacement.CHENILLES);
                    Slatch.partie.getJoueur(vNumJoueur).getListeUnite().add(vuml);
                    Slatch.partie.getTerrain()[pX][pY].setUnite(vuml);
                    vJoueur.setArgent(vArgentJv + TypeUnite.UML.getPrix());
                    vuml.attaque(true);
                    vuml.deplacee(true);
                    vuml=null;
                }
                break;
            }
    }
}
