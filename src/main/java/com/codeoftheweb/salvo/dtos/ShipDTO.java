package com.codeoftheweb.salvo.dtos;

import java.util.List;

public class ShipDTO {

    private String type;

    private List<String> locations;

    public ShipDTO(){}

    public ShipDTO(String type, List<String> locations){
        this.type = type;
        this.locations = locations;
    }

    public String getType() {
        return type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
