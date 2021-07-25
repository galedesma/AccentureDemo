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

    public ScoreDTO(){}

    public ScoreDTO(double score, long player, Date finishDate){
        this.score = score;
        this.player = player;
        this.finishDate = finishDate;
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

    public void setScore(double score) {
        this.score = score;
    }

    public void setPlayer(long player) {
        this.player = player;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }
}
