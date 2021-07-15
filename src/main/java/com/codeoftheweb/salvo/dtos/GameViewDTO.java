package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class GameViewDTO {

    private long id;

    private Date created;

    private String gameState;

    private Set<GamePlayerDTO> gamePlayers;

    public GameViewDTO(Game game){
        this.id = game.getGameId();
        this.created = game.getGameDate();
        this.gameState = "PLACESHIPS";
        this.gamePlayers = game.getGamePlayers().stream().map(gp -> new GamePlayerDTO(gp)).collect(Collectors.toSet());
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
}
