package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Score;

import java.util.Date;
import java.util.Optional;

public class ScoreDTO {

    private double score;

    private long player;

    private Date finishDate;

    public ScoreDTO(GamePlayer gamePlayer){
        Optional<Score> score = gamePlayer.getScore();
        if(score.isEmpty()){
            this.score = 0;
        } else {
            this.player = score.get().getPlayer().getId();
            this.score = score.get().getScore();
            this.finishDate = score.get().getFinishDate();
        }
    }

    public double getScore() {
        return score;
    }

    public long getPlayer() {
        return player;
    }

    public Date getFinishDate() {
        return finishDate;
    }
}
