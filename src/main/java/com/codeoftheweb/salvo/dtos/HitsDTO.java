package com.codeoftheweb.salvo.dtos;

import java.util.ArrayList;
import java.util.List;

public class HitsDTO {

    private List<String> self;

    private List<String> opponent;

    public HitsDTO(){
        this.self = new ArrayList<>();
        this.opponent = new ArrayList<>();
    }

    public List<String> getSelf() {
        return self;
    }

    public List<String> getOpponent() {
        return opponent;
    }
}
