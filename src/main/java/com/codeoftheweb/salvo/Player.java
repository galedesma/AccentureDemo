package com.codeoftheweb.salvo;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Player {

    public long getUserId() {
        return userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long userId;
    private String userName;

    @OneToMany(mappedBy = "player", fetch =  FetchType.EAGER)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    public Player(){

    }

    public Player(String user){
     userName = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }


//    public List<Game> getGames(){
//        return gamePlayers.stream().map(GamePlayer::getGame).collect(Collectors.toList());
//    }
}
