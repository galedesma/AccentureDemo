package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
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
    private long userId;
    private String userName;

    @OneToMany(mappedBy = "player", fetch =  FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch =  FetchType.EAGER)
    Set<Score> scores = new HashSet<>();

    public Player(){

    }

    public Player(String user){
     userName = user;
    }

    public String getUserName() {
        return userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score){
        //score.setPlayer(this);
        scores.add(score);
    }

    public Set<Score> getScores(){
        return scores;
    }

//    public Score getScore(Game game){
//        Optional<Score> myScore = this.scores.stream().filter(score -> score.getGame().getGameId() == game.getGameId()).findFirst();
//
//        if(myScore.isEmpty()){
//            return null;
//        } else {
//            return myScore.get();
//        }
//    }

    public Optional<Score> getScore(Game game){
        return this.scores.stream().filter(score -> score.getGame().getGameId() == game.getGameId()).findFirst();
    }


//    public List<Game> getGames(){
//        return gamePlayers.stream().map(GamePlayer::getGame).collect(Collectors.toList());
//    }
}
