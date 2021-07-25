package com.codeoftheweb.salvo.dtos;

import java.util.*;

public class DamageReportDTO {

    private int turn;

    private List<String> hitLocations;

    private Map<String, Integer> damages = new LinkedHashMap<>();

    private int missed;

    public DamageReportDTO(){}

    public DamageReportDTO(int turn, List<String> hitLocations, Map<String, Integer> damages, int missed){
        this.turn = turn;
        this.hitLocations = hitLocations;
        this.damages = damages;
        this.missed = missed;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getHitLocations() {
        return hitLocations;
    }

    public Map<String, Integer> getDamages() {
        return damages;
    }

    public int getMissed() {
        return missed;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setHitLocations(List<String> hitLocations) {
        this.hitLocations = hitLocations;
    }

    public void setDamages(Map<String, Integer> damages) {
        this.damages = damages;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }
}
