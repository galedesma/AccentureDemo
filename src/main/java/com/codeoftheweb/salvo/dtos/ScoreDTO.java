package com.codeoftheweb.salvo.dtos;

import java.util.Date;

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
