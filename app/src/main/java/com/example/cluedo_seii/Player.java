package com.example.cluedo_seii;


import com.example.cluedo_seii.spielbrett.GameboardElement;

import java.io.Serializable;
import java.util.LinkedList;

public class Player implements Serializable {

    private int id;
    private LinkedList<Card> playerCards;
    private GameboardElement position;
    private String IP;
    private GameCharacter playerCharacter;
    private Boolean madeFalseAccusation;
    private Boolean cheated;
    //private Notepad notepad;

    public Player(int id , String IP, GameCharacter playerCharacter){
        this.id = id;
        playerCards = new LinkedList<>();
        this.position = playerCharacter.getStartingPoint();
        this.IP = IP;
        this.playerCharacter = playerCharacter;
        madeFalseAccusation = false;
        cheated = false;
    }

    public GameboardElement getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public LinkedList<Card> getPlayerCards() {
        return playerCards;
    }

    public String getIP() {
        return IP;
    }

    public GameCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public void setMadeFalseAccusation(Boolean madeFalseAccusation) {
        this.madeFalseAccusation = madeFalseAccusation;
    }


    public Boolean getMadeFalseAccusation() {
        return madeFalseAccusation;
    }

    public void setCheated(){
        cheated = true;
}

    public Boolean getCheated(){return cheated;}

    /*public Notepad getNotepad() {
        return notepad;
    }*/

    public void addCard(Card card){
        playerCards.add(card);
    }


    //Verdächtigungsfunktion
    public LinkedList<String> suspect(String suspectedCulprit, String suspectedWeapon, String suspectedLocation, LinkedList<Player> players){

        LinkedList<String>wrongSuspicions = new LinkedList<>();

        for(Player player: players){

            if(suspectedCulprit.equals(player.getPlayerCharacter().getName())){

                for(Card card: player.getPlayerCards())
                {
                    if(card.getDesignation().equals(suspectedCulprit)){
                        wrongSuspicions.add(card.getDesignation());
                    }

                    if(card.getDesignation().equals(suspectedWeapon)){
                        wrongSuspicions.add(card.getDesignation());

                    }

                    if (card.getDesignation().equals(suspectedLocation)) {
                        wrongSuspicions.add(card.getDesignation());
                    }
                }

            }

        }

        return wrongSuspicions;
    }

    public void setPosition(GameboardElement position) {
        this.position = position;
    }

    //Anklagefunktion
    public boolean accuse(String accusedPerson, String accusedWeapon, String accusedLocation, InvestigationFile investigationFile){

        if(investigationFile.getCulprit().getDesignation().equals(accusedPerson)
                && investigationFile.getWeapon().getDesignation().equals(accusedWeapon)
                && investigationFile.getRoom().getDesignation().equals(accusedLocation))
        {
            return true;
        }

        else{
           madeFalseAccusation = true;
           return  false;
        }
    }


}
