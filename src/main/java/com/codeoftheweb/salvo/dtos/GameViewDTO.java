package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class GameViewDTO {

    private long id;

    private Date created;

    private String gameState;

    private Set<GamePlayerDTO> gamePlayers;

    private Set<ShipDTO> ships;

    private Set<SalvoDTO> salvoes;

    private HitsDTO hits;

    public GameViewDTO(Game game, GamePlayer gamePlayer){
        this.id = game.getGameId();
        this.created = game.getGameDate();
        this.gameState = "PLACESHIPS";
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

    public String getGameState() {
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
}
