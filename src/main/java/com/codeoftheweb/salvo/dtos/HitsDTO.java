package com.codeoftheweb.salvo.dtos;

import java.util.List;

public class HitsDTO {

    private List<Object> self;
    private List<Object> opponent;

    public HitsDTO(){}

    public HitsDTO(List<Object> self, List<Object> opponent){
        this.self = self;
        this.opponent = opponent;
    }

    public List<Object> getSelf() {
        return self;
    }

    public List<Object> getOpponent() {
        return opponent;
    }

    public void setSelf(List<Object> self) {
        this.self = self;
    }

    public void setOpponent(List<Object> opponent) {
        this.opponent = opponent;
    }
}
