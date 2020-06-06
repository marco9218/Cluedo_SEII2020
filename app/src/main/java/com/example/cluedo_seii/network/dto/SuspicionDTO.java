package com.example.cluedo_seii.network.dto;

import com.example.cluedo_seii.Card;
import com.example.cluedo_seii.Suspicion;

import java.util.LinkedList;

public class SuspicionDTO extends RequestDTO {
    private Suspicion suspicion;

    public void setSuspicion(Suspicion suspicion) {
        this.suspicion = suspicion;
    }

    public Suspicion getSuspicion() {
        return suspicion;
    }
}
