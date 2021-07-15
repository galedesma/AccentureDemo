package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.dtos.*;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import com.codeoftheweb.salvo.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getAllGames(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        List<Object> auxList = new ArrayList<>();
        List<Game> games = gameRepository.findAll();
        for(Game game: games){
            auxList.add(new GameDTO(game));
        }
        if(authentication != null){
            dto.put("player", new PlayerDTO(authenticateUser(authentication)));
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
            return new ResponseEntity<>(Utils.getDefaultDTO("gpid", newGP.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<?> placeShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication){

        Optional<GamePlayer> currentGP = gpRepository.findById(gamePlayerId);

        if(authentication == null){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Debes estar loggeado"), HttpStatus.UNAUTHORIZED);
        } else if(currentGP.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Este jugador no existe"), HttpStatus.UNAUTHORIZED);
        } else if(authenticateUser(authentication).getGamePlayers().contains(currentGP.get())){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "No puedes participar en este juego"), HttpStatus.UNAUTHORIZED);
        } else if(currentGP.get().getShips().size() > 0) {
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Este jugador ya posee naves colocadas"), HttpStatus.FORBIDDEN);
        } else if(ships.size() != 5){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Se deben colocar 5 barcos exactamente"), HttpStatus.FORBIDDEN);
        } else {
            for(Ship ship: ships){
                currentGP.get().addShip(ship);
                shipRepository.save(ship);
            }
            return new ResponseEntity<>(Utils.getDefaultDTO("OK", "Barcos añadidos"), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path= "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long nn, Authentication authentication){

        Optional<Game> currentGame = gameRepository.findById(nn);

        if(authentication == null){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Debe iniciar sesión"), HttpStatus.UNAUTHORIZED);
        } else if(currentGame.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Este juego no existe"), HttpStatus.FORBIDDEN);
        } else if(currentGame.get().getGamePlayers().size() >= 2) {
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Sala llena"), HttpStatus.FORBIDDEN);
        } else {
            GamePlayer newGP = new GamePlayer(currentGame.get(), authenticateUser(authentication));
            gpRepository.save(newGP);

            return new ResponseEntity<>(Utils.getDefaultDTO("gpid", newGP.getId()), HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication){
        GamePlayer gamePlayer = this.gpRepository.getById(gamePlayerId);
        Game game = this.gameRepository.getById(gamePlayer.getGame().getGameId());

        if(authenticateUser(authentication).getGamePlayers().contains(gamePlayer)) {
            ObjectMapper oMapper = new ObjectMapper();
            Map<String, Object> dto = oMapper.convertValue(new GameViewDTO(game), Map.class);
            dto.put("ships", gamePlayer.getShips().stream().map(ship -> new ShipDTO(ship)));
            dto.put("salvoes", game.getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(salvo -> new SalvoDTO(salvo))));
            dto.put("hits", new HitsDTO());

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "No tienes permiso para ver este juego"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String email, @RequestParam String password){
        if(email.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Debe ingresar un email"), HttpStatus.FORBIDDEN);
        }

        if(password.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Debe ingresar una contraseña"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(email);
        if(player != null){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "Este mail ya esta en uso"), HttpStatus.CONFLICT);
        }

        Player newPlayer = new Player(email, passwordEncoder.encode(password));
        playerRepository.save(newPlayer);

        return new ResponseEntity<>(Utils.getDefaultDTO("username", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    private Player authenticateUser(Authentication authentication){
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return null;
        } else {
            return playerRepository.findByUserName(authentication.getName());
        }
    }
}
