package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class GameDTO {

    private long id;

    private Date created;

    private Set<GamePlayerDTO> gamePlayers;

    private Set<ScoreDTO> scores;

    public GameDTO(Game game){
        this.id = game.getGameId();
        this.created = game.getGameDate();
        this.gamePlayers = game.getGamePlayers().stream().map(gp -> new GamePlayerDTO(gp)).collect(Collectors.toSet());
        this.scores = game.getGamePlayers().stream().map(gp -> new ScoreDTO(gp)).collect(Collectors.toSet());
    }

    public long getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public Set<GamePlayerDTO> getGamePlayers() {
        return gamePlayers;
    }

    public Set<ScoreDTO> getScores() {
        return scores;
    }
}
