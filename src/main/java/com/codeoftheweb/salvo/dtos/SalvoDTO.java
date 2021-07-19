package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Salvo;

import java.util.List;

public class SalvoDTO {

    private int turn;

    private long player;

    private List<String> locations;

    public SalvoDTO(Salvo salvo){
        this.turn = salvo.getTurn();
        this.player = salvo.getGamePlayer().getPlayer().getId();
        this.locations = salvo.getSalvoLocations();
    }

    public int getTurn() {
        return turn;
    }

    public long getPlayer() {
        return player;
    }

    public List<String> getLocations() {
        return locations;
    }
}
