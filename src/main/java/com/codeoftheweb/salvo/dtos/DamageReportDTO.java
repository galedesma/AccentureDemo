package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.models.Ship;

import java.util.*;
import java.util.stream.Collectors;

public class DamageReportDTO {

    private int turn;

    private List<String> hitLocations;

    private Map<String, Integer> damages = new LinkedHashMap<>();

    private int missed;

    private int carrierCounter = 0;
    private int battleshipCounter = 0;
    private int destroyerCounter = 0;
    private int submarineCounter = 0;
    private int patrolboatCounter = 0;

    public DamageReportDTO(Salvo salvo, GamePlayer opponent){
        this.turn = salvo.getTurn();

        List<String> ownSalvo = salvo.getSalvoLocations().stream().collect(Collectors.toList());
        List<String> oppShips = opponent.getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());

        this.hitLocations = oppShips.stream().distinct().filter(shipPosition -> ownSalvo.contains(shipPosition)).collect(Collectors.toList());

        damages.put("carrierHits", countHits(this.hitLocations, opponent, "carrier"));
        damages.put("battleshipHits", countHits(this.hitLocations, opponent, "battleship"));
        damages.put("destroyerHits", countHits(this.hitLocations, opponent, "destroyer"));
        damages.put("submarineHits", countHits(this.hitLocations, opponent, "submarine"));
        damages.put("patrolboatHits", countHits(this.hitLocations, opponent, "patrolboat"));

        damages.put("carrier", carrierCounter);
        damages.put("battleship", battleshipCounter);
        damages.put("destroyer", destroyerCounter);
        damages.put("submarine", submarineCounter);
        damages.put("patrolboat", patrolboatCounter);

        this.missed = salvo.getSalvoLocations().size() - this.hitLocations.size();
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

    private int countHits(List<String> impact, GamePlayer target, String type){
        int count = 0;

        Set<Ship> ships = target.getShips();
        Optional<Ship> targetShip = ships.stream().filter(ship -> ship.getType().equals(type)).findFirst();

        if(targetShip.isEmpty()){
            return count;
        }

        List<String> locations = targetShip.get().getShipLocations();
        count = (int) impact.stream().filter(i -> locations.contains(i)).count();

        if(type.equals("patrolboat")){
            this.patrolboatCounter = count;
        }

        if(type.equals("submarine")){
            this.submarineCounter = count;
        }

        if(type.equals("destroyer")){
            this.destroyerCounter = count;
        }

        if(type.equals("battleship")){
            this.battleshipCounter = count;
        }

        if(type.equals("carrier")){
            this.carrierCounter = count;
        }

        return count;
    }
}
