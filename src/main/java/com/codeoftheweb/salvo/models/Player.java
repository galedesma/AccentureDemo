package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "player", fetch =  FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch =  FetchType.EAGER)
    Set<Score> scores = new HashSet<>();

    public Player(){

    }

    public Player(String user, String pass){
     userName = user;
     password = pass;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public long getId() {
        return id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score){
        scores.add(score);
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<Score> getScores(){
        return scores;
    }

    public Optional<Score> getScore(Game game){
        return this.scores.stream().filter(score -> score.getGame().getGameId() == game.getGameId()).findFirst();
    }


}
