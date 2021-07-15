package com.codeoftheweb.salvo.dtos;

import com.codeoftheweb.salvo.models.Ship;

import java.util.List;

public class ShipDTO {

    private String type;

    private List<String> locations;

    public ShipDTO(Ship ship){
        this.type = ship.getType();
        this.locations = ship.getShipLocations();
    }

    public String getType() {
        return type;
    }

    public List<String> getLocations() {
        return locations;
    }
}
