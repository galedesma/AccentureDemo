package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.models.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HitsDTO {

    private List<String> self;

    private List<String> opponent;

    public HitsDTO(){
        this.self = new ArrayList<>();
        this.opponent = new ArrayList<>();
    }

    public HitsDTO(Game game, GamePlayer gamePlayer){

        Optional<GamePlayer> opp = game.getGamePlayers().stream().filter(gp -> gp != gamePlayer).findFirst();

        if(opp.isEmpty()){
            this.self = new ArrayList<>();
            this.opponent = new ArrayList<>();
        } else {
            List<String> ownShips = gamePlayer.getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());
            List<String> ownSalvoes = gamePlayer.getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList());

            List<String> oppShips = opp.get().getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());
            List<String> oppSalvoes = opp.get().getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList());

            this.self = ownShips.stream().distinct().filter(shipPosition -> oppSalvoes.contains(shipPosition)).collect(Collectors.toList());
            this.opponent = oppShips.stream().distinct().filter(shipPosition -> ownSalvoes.contains(shipPosition)).collect(Collectors.toList());
        }
    }

    public List<String> getSelf() {
        return self;
    }

    public List<String> getOpponent() {
        return opponent;
    }
}
