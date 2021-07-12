package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gpRepository;

    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        List<Object> auxList = new ArrayList<>();
        List<Game> games = gameRepository.findAll();
        for(Game game: games){
            auxList.add(getDTO(game));
        }
        if(authentication != null){
            dto.put("player", getPlayerDTO(authenticateUser(authentication)));
        } else {
            dto.put("player", "Guest");
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

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String username, @RequestParam String password){
        if(username.isEmpty()){
            return new ResponseEntity<>(getDefaultDTO("error", "Debe ingresar un email"), HttpStatus.FORBIDDEN);
        }

        if(password.isEmpty()){
            return new ResponseEntity<>(getDefaultDTO("error", "Debe ingresar una contraseña"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(username);
        if(player != null){
            return new ResponseEntity<>(getDefaultDTO("error", "Este mail ya esta en uso"), HttpStatus.CONFLICT);
        }

        Player newPlayer = new Player(username, passwordEncoder.encode(password));
        playerRepository.save(newPlayer);

        return new ResponseEntity<>(getDefaultDTO("username", newPlayer.getUserName()), HttpStatus.CREATED);
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

    private Map<String, Object> getDefaultDTO(String key, Object value){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }

    private Player authenticateUser(Authentication authentication){
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        } else {
            return playerRepository.findByUserName(authentication.getName());
        }
    }
}
