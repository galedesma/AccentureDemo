package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.dtos.*;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.utils.GameState;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService{

    private GameState result;

    @Override
    public GameDTO makeGameDTO(Game game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getGameId());
        dto.setCreated(game.getGameDate());
        dto.setGamePlayers(getGamePlayersDTO(game));
        dto.setScores(getScoresDTO(game));

        return dto;
    }

    @Override
    public PlayerDTO makePlayerDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setEmail(player.getUserName());

        return dto;
    }

    @Override
    public GameViewDTO makeGameViewDTO(Game game, GamePlayer gamePlayer) {
        GameViewDTO aux = new GameViewDTO();
        aux.setId(game.getGameId());
        aux.setCreated(game.getGameDate());
        aux.setGameState(setGameState(game, gamePlayer));
        aux.setGamePlayers(getGamePlayersDTO(game));
        aux.setShips(getShipsDTO(gamePlayer));
        aux.setSalvoes(getSalvoesDTO(game));
        aux.setHits(getHitsDTO(game, gamePlayer));

        return aux;
    }

    private Set<GamePlayerDTO> getGamePlayersDTO(Game game){
        return game.getGamePlayers()
                .stream()
                .map(gp -> {
                    GamePlayerDTO aux = new GamePlayerDTO();
                    aux.setId(gp.getId());
                    aux.setPlayer(new PlayerDTO(gp.getPlayer()));

                    return aux;
                })
                .collect(Collectors.toSet());
    }

    private Set<ScoreDTO> getScoresDTO(Game game){
        return game.getGamePlayers()
                .stream()
                .map(gp -> {
                    ScoreDTO aux = new ScoreDTO();
                    Optional<Score> score = gp.getScore();

                    if(score.isEmpty()){
                        aux.setScore(0);
                    } else {
                        aux.setPlayer(score.get().getPlayer().getId());
                        aux.setScore(score.get().getScore());
                        aux.setFinishDate(score.get().getFinishDate());
                    }

                    return aux;
                })
                .collect(Collectors.toSet());
    }

    private GameState setGameState(Game game, GamePlayer gamePlayer) {
        Optional<GamePlayer> opp = game.getGamePlayers().stream().filter(gp -> gp != gamePlayer).findFirst();

        if(gamePlayer.getShips().size() == 0){
            return GameState.PLACESHIPS;
        }

        if(opp.isEmpty()){
            return GameState.WAITINGFOROPP;
        }

        if(opp.get().getShips().size() == 0 || gamePlayer.getSalvoes().size() > opp.get().getSalvoes().size()){
            return GameState.WAIT;
        }

        if(isGameOver(gamePlayer, opp.get())){
            return result;
        }

        return GameState.PLAY;
    }

    private boolean isGameOver(GamePlayer self, GamePlayer opponent){
        List<String> selfShips = self.getShips()
                .stream()
                .flatMap(ship -> ship.getShipLocations().stream())
                .collect(Collectors.toList());
        List<String> oppShips = opponent.getShips()
                .stream()
                .flatMap(ship -> ship.getShipLocations().stream())
                .collect(Collectors.toList());

        List<String> selfSalvo = self.getSalvoes()
                .stream()
                .flatMap(salvo -> salvo.getSalvoLocations().stream())
                .collect(Collectors.toList());
        List<String> oppSalvo = opponent.getSalvoes()
                .stream()
                .flatMap(salvo -> salvo.getSalvoLocations().stream())
                .collect(Collectors.toList());

        List<String> oppHits = selfShips
                .stream()
                .filter(shipPosition -> oppSalvo.contains(shipPosition))
                .collect(Collectors.toList());
        List<String> selfHits = oppShips
                .stream()
                .filter(shipPosition -> selfSalvo.contains(shipPosition))
                .collect(Collectors.toList());

        if(self.getSalvoes().size() == opponent.getSalvoes().size()){
            if((oppHits.size() == selfHits.size()) && oppHits.size() != 0 && oppHits.size() == selfShips.size()){
                result = GameState.TIE;
                return true;
            }

            if(selfShips.size() == oppHits.size()){
                result = GameState.LOST;
                return true;
            }

            if (oppShips.size() == selfHits.size()){
                result = GameState.WON;
                return true;
            }
        }
        return false;
    }

    private Set<ShipDTO> getShipsDTO(GamePlayer gamePlayer){
        return gamePlayer.getShips()
                .stream()
                .map(ship -> {
                    ShipDTO aux = new ShipDTO();
                    aux.setType(ship.getType());
                    aux.setLocations(ship.getShipLocations());

                    return aux;
                })
                .collect(Collectors.toSet());
    }

    private Set<SalvoDTO> getSalvoesDTO(Game game){
        return game.getGamePlayers()
                .stream()
                .flatMap(gp -> gp.getSalvoes()
                    .stream()
                    .map(salvo -> {
                        SalvoDTO aux = new SalvoDTO();
                        aux.setTurn(salvo.getTurn());
                        aux.setPlayer(salvo.getGamePlayer().getPlayer().getId());
                        aux.setLocations(salvo.getSalvoLocations());

                        return aux;
                    }))
                .collect(Collectors.toSet());
    }

    private HitsDTO getHitsDTO(Game game, GamePlayer gamePlayer){
        HitsDTO aux = new HitsDTO();
        Optional<GamePlayer> opp = game.getGamePlayers().stream().filter(gp -> gp != gamePlayer).findFirst();

        if(opp.isEmpty()){
            aux.setSelf(new ArrayList<>());
            aux.setOpponent(new ArrayList<>());
        } else {
            aux.setSelf(getReportDTO(opp.get(), gamePlayer));
            aux.setOpponent(getReportDTO(gamePlayer, opp.get()));
        }

        return aux;
    }

    private List<Object> getReportDTO(GamePlayer gamePlayer, GamePlayer target){
        int carrierTotal = 0;
        int battleshipTotal = 0;
        int destroyerTotal = 0;
        int submarineTotal = 0;
        int patrolboatTotal = 0;

        List<Salvo> salvoes = Utils.fromSetToList(gamePlayer.getSalvoes());

        List<Object> aux = new ArrayList<>();

        for(Salvo salvo: salvoes){
            DamageReportDTO dto = new DamageReportDTO();
            dto.setTurn(salvo.getTurn());

            List<String> hitsThisTurn = getHitLocations(salvo, target);

            dto.setHitLocations(hitsThisTurn);
            dto.setDamages(getDamageMap(hitsThisTurn, target));
            dto.setMissed(salvo.getSalvoLocations().size() - hitsThisTurn.size());

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

    private List<String> getHitLocations(Salvo salvo, GamePlayer target){
        List<String> ownSalvo = salvo.getSalvoLocations()
                .stream()
                .collect(Collectors.toList());

        List<String> oppShips = target.getShips()
                .stream()
                .flatMap(ship -> ship.getShipLocations()
                        .stream())
                .collect(Collectors.toList());

        return oppShips
                .stream()
                .distinct()
                .filter(shipPosition -> ownSalvo.contains(shipPosition))
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getDamageMap(List<String> hits, GamePlayer target){
        Map<String, Integer> aux = new LinkedHashMap<>();
        int carrierCounter = countHits(hits, target, "carrier");
        int battleshipCounter = countHits(hits, target, "battleship");
        int destroyerCounter = countHits(hits, target, "destroyer");
        int submarineCounter = countHits(hits, target, "submarine");
        int patrolboatCounter = countHits(hits, target, "patrolboat");

        aux.put("carrierHits", carrierCounter);
        aux.put("battleshipHits", battleshipCounter);
        aux.put("destroyerHits", destroyerCounter);
        aux.put("submarineHits", submarineCounter);
        aux.put("patrolboatHits", patrolboatCounter);

        aux.put("carrier", carrierCounter);
        aux.put("battleship", battleshipCounter);
        aux.put("destroyer", destroyerCounter);
        aux.put("submarine", submarineCounter);
        aux.put("patrolboat", patrolboatCounter);

        return aux;
    }

    private int countHits(List<String> impact, GamePlayer target, String type){
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
