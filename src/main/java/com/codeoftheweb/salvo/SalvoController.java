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

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getAllGames(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        List<Object> auxList = new ArrayList<>();
        List<Game> games = gameRepository.findAll();
        for(Game game: games){
            auxList.add(getGameDTO(game));
        }
        if(authentication != null){
            dto.put("player", getPlayerDTO(authenticateUser(authentication)));
        } else {
            dto.put("player", "Guest");
        }
        dto.put("games", auxList);
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
        if(authentication != null){
            Game newGame = new Game(new Date());
            gameRepository.save(newGame);

            GamePlayer newGP = new GamePlayer(newGame, authenticateUser(authentication));
            gpRepository.save(newGP);
            return new ResponseEntity<>(getDefaultDTO("gpid", newGP.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(getDefaultDTO("error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path= "game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long nn, Authentication authentication){

        Optional<Game> currentGame = gameRepository.findById(nn);

        if(authentication == null){
            return new ResponseEntity<>(getDefaultDTO("error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        } else if(currentGame.isEmpty()){
            return new ResponseEntity<>(getDefaultDTO("error", "Este juego no existe"), HttpStatus.FORBIDDEN);
        } else if(currentGame.get().getGamePlayers().size() >= 2) {
            return new ResponseEntity<>(getDefaultDTO("error", "Sala llena"), HttpStatus.FORBIDDEN);
        } else {
            GamePlayer newGP = new GamePlayer(currentGame.get(), authenticateUser(authentication));
            gpRepository.save(newGP);

            return new ResponseEntity<>(getDefaultDTO("gpid", newGP.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication){
        GamePlayer gamePlayer = this.gpRepository.getById(gamePlayerId);
        Game game = this.gameRepository.getById(gamePlayer.getGame().getGameId());

        if(authenticateUser(authentication).getGamePlayers().contains(gamePlayer)) {
            Map<String, Object> dto = getGameDTONoScores(game);
            dto.put("ships", gamePlayer.getShips().stream().map(ship -> getShipDTO(ship)));
            dto.put("salvoes", game.getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(salvo -> getSalvoDTO(salvo))));
            dto.put("hits", getHitsDTO());

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(getDefaultDTO("error", "No tienes permiso para ver este juego"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String email, @RequestParam String password){
        if(email.isEmpty()){
            return new ResponseEntity<>(getDefaultDTO("error", "Debe ingresar un email"), HttpStatus.FORBIDDEN);
        }

        if(password.isEmpty()){
            return new ResponseEntity<>(getDefaultDTO("error", "Debe ingresar una contraseña"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(email);
        if(player != null){
            return new ResponseEntity<>(getDefaultDTO("error", "Este mail ya esta en uso"), HttpStatus.CONFLICT);
        }

        Player newPlayer = new Player(email, passwordEncoder.encode(password));
        playerRepository.save(newPlayer);

        return new ResponseEntity<>(getDefaultDTO("username", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> getGameDTO(Game game){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getGameId());
        dto.put("created", game.getGameDate());
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGamePlayerDTO).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gp -> getScoreDTO(gp)));
        return dto;
    }

    private Map<String, Object> getGameDTONoScores(Game game){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getGameId());
        dto.put("created", game.getGameDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", game.getGamePlayers().stream().map(this::getGamePlayerDTO).collect(Collectors.toList()));

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

    private Map<String, Object> getHitsDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("self", new ArrayList<>());
        dto.put("opponent", new ArrayList<>());

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
