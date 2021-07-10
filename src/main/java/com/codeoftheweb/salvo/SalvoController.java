package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gpRepository;

    @RequestMapping("/games")
    public Map<String, Object> getAllGames(){
        Map<String, Object> dto = new LinkedHashMap<>();
        List<Object> auxList = new ArrayList<>();
        List<Game> games = gameRepository.findAll();
        for(Game game: games){
            auxList.add(getDTO(game));
        }
        dto.put("games", auxList);
        return dto;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> getGameView(@PathVariable Long gamePlayerId){
        GamePlayer gamePlayer = this.gpRepository.getById(gamePlayerId);
        Game game = this.gameRepository.getById(gamePlayer.getGame().getGameId());

        Map<String, Object> dto = getDTO(game);
        dto.put("ships", gamePlayer.getShips().stream().map(ship -> getShipDTO(ship)));
        dto.put("salvoes", game.getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(salvo -> getSalvoDTO(salvo))));
        return dto;
    }

    private Map<String, Object> getDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getGameId());
        dto.put("created", game.getGameDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGamePlayerDTO).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gp -> getScoreDTO(gp)));
        return dto;
    }

    private Map<String, Object> getGamePlayerDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", getPlayerDTO(gamePlayer.getPlayer()));
        return dto;
    }

    private Map<String, Object> getPlayerDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    private Map<String, Object> getShipDTO(Ship ship){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());
        return dto;
    }

    private Map<String, Object> getSalvoDTO(Salvo salvo){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer().getId());
        dto.put("locations", salvo.getLocations());
        return dto;
    }

    private Map<String, Object> getScoreDTO(GamePlayer gamePlayer){
        Map<String, Object> dto = new LinkedHashMap<>();
        Optional<Score> score = gamePlayer.getScore();
        if(score.isEmpty()){
            dto.put("score", "Este juego no tiene puntaje");
        } else{
            dto.put("player", score.get().getPlayer().getId());
            dto.put("score", score.get().getScore());
            dto.put("finishDate", score.get().getFinishDate());
        }
        return dto;
    }
}
