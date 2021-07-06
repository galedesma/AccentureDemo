package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long gameId;
    private Date gameDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    public Game(){

    }

    public Game(Date date){
        this.gameDate = date;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public List<Player> getPlayers(){
        return gamePlayers.stream().map(GamePlayer::getPlayer).collect(Collectors.toList());
    }

    public long getGameId() {
        return gameId;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
}
