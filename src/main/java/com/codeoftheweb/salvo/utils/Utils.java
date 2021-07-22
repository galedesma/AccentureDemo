package com.codeoftheweb.salvo.utils;

import com.codeoftheweb.salvo.dtos.DamageReportDTO;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.models.Ship;

import java.util.*;

public class Utils {

    public static Map<String, Object> getDefaultDTO(String key, Object value){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }

    public static List<Salvo> fromSetToList(Set<Salvo> salvos) {
        List<Salvo> aux = new ArrayList<>();
        for (int i = 1; i <= salvos.size(); i++) {
            int finalI = i;
            Optional<Salvo> salvo = salvos.stream().filter(s -> s.getTurn() == finalI).findFirst();
            if (salvo.isPresent()) {
                aux.add(salvo.get());
            }
        }
        return aux;
    }

    public static List<Object> getReport(GamePlayer gamePlayer, GamePlayer target){
        int carrierTotal = 0;
        int battleshipTotal = 0;
        int destroyerTotal = 0;
        int submarineTotal = 0;
        int patrolboatTotal = 0;

        List<Salvo> salvoes = Utils.fromSetToList(gamePlayer.getSalvoes());

        List<Object> aux = new ArrayList<>();

        for(Salvo salvo: salvoes){
            DamageReportDTO dto = new DamageReportDTO(salvo, target);

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
            aux.add(dto);
        }

        return aux;
    }

    public static int countHits(List<String> impact, GamePlayer target, String type){
        int count = 0;

        Set<Ship> ships = target.getShips();
        Optional<Ship> targetShip = ships.stream().filter(ship -> ship.getType().equals(type)).findFirst();

        if(targetShip.isEmpty()){
            return count;
        }

        List<String> locations = targetShip.get().getShipLocations();
        count = (int) impact.stream().filter(i -> locations.contains(i)).count();

        return count;
    }
}
