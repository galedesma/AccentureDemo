package com.codeoftheweb.salvo.dtos;

import java.util.List;

public class SalvoDTO {

    private int turn;

    private long player;

    private List<String> locations;

    public SalvoDTO(){}

    public SalvoDTO(int turn, long player, List<String> locations){
        this.turn = turn;
        this.player = player;
        this.locations = locations;
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

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setPlayer(long player) {
        this.player = player;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
