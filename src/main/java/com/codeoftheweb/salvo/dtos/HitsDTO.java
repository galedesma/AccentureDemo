package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HitsDTO {

    private List<Object> self = new ArrayList<>();
    private List<Object> opponent = new ArrayList<>();

    private int carrierTotal = 0;
    private int battleshipTotal = 0;
    private int destroyerTotal = 0;
    private int submarineTotal = 0;
    private int patrolboatTotal = 0;

    private int oppCarrierTotal = 0;
    private int oppBattleshipTotal = 0;
    private int oppDestroyerTotal = 0;
    private int oppSubmarineTotal = 0;
    private int oppPatrolboatTotal = 0;

    public HitsDTO(Game game, GamePlayer gamePlayer){

        Optional<GamePlayer> opp = game.getGamePlayers().stream().filter(gp -> gp != gamePlayer).findFirst();

        if(opp.isEmpty()){
            this.self = new ArrayList<>();
            this.opponent = new ArrayList<>();
        } else {
            List<Salvo> ownSalvoes = Utils.fromSetToList(gamePlayer.getSalvoes());
            List<Salvo> oppSalvoes = Utils.fromSetToList(opp.get().getSalvoes());

            for(Salvo salvo: ownSalvoes){
                DamageReportDTO dto = new DamageReportDTO(salvo, opp.get());

                carrierTotal = carrierTotal + dto.getDamages().get("carrierHits");
                battleshipTotal = battleshipTotal + dto.getDamages().get("battleshipHits");
                submarineTotal = submarineTotal + dto.getDamages().get("submarineHits");
                destroyerTotal = destroyerTotal + dto.getDamages().get("destroyerHits");
                patrolboatTotal = patrolboatTotal + dto.getDamages().get("patrolboatHits");

                if(dto.getTurn() != 1){
                    dto.getDamages().put("carrier", carrierTotal);
                    dto.getDamages().put("battleship", battleshipTotal);
                    dto.getDamages().put("destroyer", destroyerTotal);
                    dto.getDamages().put("submarine", submarineTotal);
                    dto.getDamages().put("patrolboat", patrolboatTotal);
                }

                this.self.add(dto);
            }

            for(Salvo salvo: oppSalvoes){
                DamageReportDTO dto = new DamageReportDTO(salvo, gamePlayer);

                oppCarrierTotal = oppCarrierTotal + dto.getDamages().get("carrierHits");
                oppBattleshipTotal = oppBattleshipTotal + dto.getDamages().get("battleshipHits");
                oppSubmarineTotal = oppSubmarineTotal + dto.getDamages().get("submarineHits");
                oppDestroyerTotal = oppDestroyerTotal + dto.getDamages().get("destroyerHits");
                oppPatrolboatTotal = oppPatrolboatTotal + dto.getDamages().get("patrolboatHits");

                if(dto.getTurn() != 1){
                    dto.getDamages().put("carrier", oppCarrierTotal);
                    dto.getDamages().put("battleship", oppBattleshipTotal);
                    dto.getDamages().put("destroyer", oppDestroyerTotal);
                    dto.getDamages().put("submarine", oppSubmarineTotal);
                    dto.getDamages().put("patrolboat", oppPatrolboatTotal);
                }

                this.opponent.add(dto);
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
