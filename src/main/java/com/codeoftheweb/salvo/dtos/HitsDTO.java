package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HitsDTO {

    private List<Object> self;
    private List<Object> opponent;

    public HitsDTO(Game game, GamePlayer gamePlayer){

        Optional<GamePlayer> opp = game.getGamePlayers().stream().filter(gp -> gp != gamePlayer).findFirst();

        if(opp.isEmpty()){
            this.self = new ArrayList<>();
            this.opponent = new ArrayList<>();
        } else {
            this.opponent = Utils.getReport(gamePlayer, opp.get());
            this.self = Utils.getReport(opp.get(), gamePlayer);
        }
    }

    public List<Object> getSelf() {
        return self;
    }

    public List<Object> getOpponent() {
        return opponent;
    }
}
