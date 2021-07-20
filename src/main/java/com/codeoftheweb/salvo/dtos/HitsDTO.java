package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class HitsDTO {

    private List<Object> self = new ArrayList<>();

    private List<Object> opponent = new ArrayList<>();

    public HitsDTO(Game game, GamePlayer gamePlayer){

        Optional<GamePlayer> opp = game.getGamePlayers().stream().filter(gp -> gp != gamePlayer).findFirst();

        if(opp.isEmpty()){
            this.self = new ArrayList<>();
            this.opponent = new ArrayList<>();
        } else {
            Set<Salvo> ownSalvoes = gamePlayer.getSalvoes();
            Set<Salvo> oppSalvoes = opp.get().getSalvoes();

            for(Salvo salvo: ownSalvoes){
                this.self.add(new DamageReportDTO(salvo, opp.get()));
            }

            for(Salvo salvo: oppSalvoes){
                this.opponent.add(new DamageReportDTO(salvo, gamePlayer));
            }
        }
    }

    public List<Object> getSelf() {
        return self;
    }

    public List<Object> getOpponent() {
        return opponent;
    }
}
