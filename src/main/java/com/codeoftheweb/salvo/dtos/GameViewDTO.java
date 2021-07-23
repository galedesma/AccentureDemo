package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.utils.GameState;

import java.util.*;
import java.util.stream.Collectors;

public class GameViewDTO {

    private long id;

    private Date created;

    private GameState gameState;

    private Set<GamePlayerDTO> gamePlayers;

    private Set<ShipDTO> ships;

    private Set<SalvoDTO> salvoes;

    private HitsDTO hits;

    private GameState result;

    public GameViewDTO(Game game, GamePlayer gamePlayer){
        this.id = game.getGameId();
        this.created = game.getGameDate();
        this.gameState = setGameState(game, gamePlayer);
        this.gamePlayers = game.getGamePlayers().stream().map(gp -> new GamePlayerDTO(gp)).collect(Collectors.toSet());
        this.ships = gamePlayer.getShips().stream().map(ship -> new ShipDTO(ship)).collect(Collectors.toSet());
        this.salvoes = game.getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(salvo -> new SalvoDTO(salvo))).collect(Collectors.toSet());
        this.hits = new HitsDTO(game, gamePlayer);
    }

    public long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public Set<ShipDTO> getShips() {
        return ships;
    }

    public Set<SalvoDTO> getSalvoes() {
        return salvoes;
    }

    public HitsDTO getHits() {
        return hits;
    }

    public GameState setGameState(Game game, GamePlayer gamePlayer) {

        Optional<GamePlayer> opp = game.getGamePlayers().stream().filter(gp -> gp != gamePlayer).findFirst();

        if(gamePlayer.getShips().size() == 0){
            return GameState.PLACESHIPS;
        }

        if(opp.isEmpty()){
            return GameState.WAITINGFOROPP;
        }

        if(opp.get().getShips().size() == 0 || gamePlayer.getSalvoes().size() > opp.get().getSalvoes().size()){
            return GameState.WAIT;
        }

        if(isGameOver(gamePlayer, opp.get())){
            return result;
        }

        return GameState.PLAY;
    }

    private boolean isGameOver(GamePlayer self, GamePlayer opponent){
        List<String> selfShips = self.getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());
        List<String> oppShips = opponent.getShips().stream().flatMap(ship -> ship.getShipLocations().stream()).collect(Collectors.toList());

        List<String> selfSalvo = self.getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList());
        List<String> oppSalvo = opponent.getSalvoes().stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList());

        List<String> oppHits = selfShips.stream().filter(shipPosition -> oppSalvo.contains(shipPosition)).collect(Collectors.toList());
        List<String> selfHits = oppShips.stream().filter(shipPosition -> selfSalvo.contains(shipPosition)).collect(Collectors.toList());

        if(self.getSalvoes().size() == opponent.getSalvoes().size()){
            if((oppHits.size() == selfHits.size()) && oppHits.size() != 0 && oppHits.size() == selfShips.size()){
                result = GameState.TIE;
                return true;
            }

            if(selfShips.size() == oppHits.size()){
                result = GameState.LOST;
                return true;
            }

            if (oppShips.size() == selfHits.size()){
                result = GameState.WON;
                return true;
            }
        }
        return false;
    }
}
