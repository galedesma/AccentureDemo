package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class DamageReportDTO {

    private int turn;

    private List<String> hitLocations;

    private Map<String, Integer> damages = new LinkedHashMap<>();

    private int missed;

    public DamageReportDTO(Salvo salvo, GamePlayer opponent){
        this.turn = salvo.getTurn();

        List<String> ownSalvo = salvo.getSalvoLocations().stream().collect(Collectors.toList());
        List<String> oppShips = opponent.getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());

        this.hitLocations = oppShips.stream().distinct().filter(shipPosition -> ownSalvo.contains(shipPosition)).collect(Collectors.toList());

        int carrierCounter = Utils.countHits(this.hitLocations, opponent, "carrier");
        int battleshipCounter = Utils.countHits(this.hitLocations, opponent, "battleship");
        int destroyerCounter = Utils.countHits(this.hitLocations, opponent, "destroyer");
        int submarineCounter = Utils.countHits(this.hitLocations, opponent, "submarine");
        int patrolboatCounter = Utils.countHits(this.hitLocations, opponent, "patrolboat");

        damages.put("carrierHits", carrierCounter);
        damages.put("battleshipHits", battleshipCounter);
        damages.put("destroyerHits", destroyerCounter);
        damages.put("submarineHits", submarineCounter);
        damages.put("patrolboatHits", patrolboatCounter);

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
}
