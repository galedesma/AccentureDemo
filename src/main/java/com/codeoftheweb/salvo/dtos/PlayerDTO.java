package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Player;

public class PlayerDTO {

    private long id;

    private String email;

    public PlayerDTO(){}

    public PlayerDTO(Player player){
        this.id = player.getId();
        this.email = player.getUserName();
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
