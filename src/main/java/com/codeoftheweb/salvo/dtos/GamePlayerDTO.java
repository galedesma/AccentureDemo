package com.codeoftheweb.salvo.dtos;

public class GamePlayerDTO {

    private long id;

    private PlayerDTO player;

    public GamePlayerDTO(){}

    public GamePlayerDTO(long id, PlayerDTO player){
        this.id = id;
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }
}
