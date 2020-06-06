package com.example.cluedo_seii;

import java.util.LinkedList;

public class Suspicion {

    private LinkedList<Card> cards;
    private Player accusee;
    private Player accuser;

    public Suspicion(LinkedList<Card> cards, Player accusee, Player accuser){
        this.cards = cards;
        this.accusee = accusee;
        this.accuser = accuser;
    }

    public LinkedList<Card> getCards() {
        return cards;
    }

    public Player getAccusee() {
        return accusee;
    }

    public Player getAccuser() {
        return accuser;
    }
}
