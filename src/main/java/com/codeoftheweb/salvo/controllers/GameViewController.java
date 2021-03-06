package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.dtos.GameViewDTO;
import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import com.codeoftheweb.salvo.service.GameService;
import com.codeoftheweb.salvo.utils.GameState;
import com.codeoftheweb.salvo.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameViewController {

    @Autowired
    private GamePlayerRepository gpRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private GameService gameService;

    @RequestMapping(path = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable Long gamePlayerId, Authentication authentication){
        Optional<GamePlayer> gamePlayer = this.gpRepository.findById(gamePlayerId);

        if(Utils.isGuest(authentication)){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must be logged in to see this"), HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayer.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This gamePlayer doesn't exist yet!"), HttpStatus.UNAUTHORIZED);
        }

        Optional<Game> game = this.gameRepository.findById(gamePlayer.get().getGame().getGameId());

        if(game.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This game doesn't exist!"), HttpStatus.UNAUTHORIZED);
        }

        if(!getCurrentLoggedUser(authentication).getGamePlayers().contains(gamePlayer.get())) {
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You are not allowed to view this game"), HttpStatus.UNAUTHORIZED);
        }

        ObjectMapper oMapper = new ObjectMapper();
        GameViewDTO currentGameView = gameService.makeGameViewDTO(game.get(), gamePlayer.get());

        if(currentGameView.getGameState() == GameState.WON){
            Score winnerScore = new Score(1.0, game.get(), gamePlayer.get().getPlayer());
            scoreRepository.save(winnerScore);
        }

        if(currentGameView.getGameState() == GameState.LOST){
            Score loserScore = new Score(0.0, game.get(), gamePlayer.get().getPlayer());
            scoreRepository.save(loserScore);
        }

        if(currentGameView.getGameState() == GameState.TIE){
            Score tieScore = new Score(0.5, game.get(), gamePlayer.get().getPlayer());
            scoreRepository.save(tieScore);
        }

        Map<String, Object> dto = oMapper.convertValue(currentGameView, Map.class);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(path= "/game/{nn}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long nn, Authentication authentication){

        Optional<Game> currentGame = gameRepository.findById(nn);

        if(Utils.isGuest(authentication)){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must be logged in to join a game"), HttpStatus.UNAUTHORIZED);
        }

        if(currentGame.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This game doesn't exist"), HttpStatus.FORBIDDEN);
        }

        if(currentGame.get().getGamePlayers().size() >= 2) {
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This game is full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer newGP = new GamePlayer(currentGame.get(), getCurrentLoggedUser(authentication));
        gpRepository.save(newGP);

        return new ResponseEntity<>(Utils.getDefaultDTO("gpid", newGP.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<?> placeShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication){

        Optional<GamePlayer> currentGP = gpRepository.findById(gamePlayerId);

        if(Utils.isGuest(authentication)){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must be logged in to do this"), HttpStatus.UNAUTHORIZED);
        }

        if(currentGP.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This game doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        if(!getCurrentLoggedUser(authentication).getGamePlayers().contains(currentGP.get())){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You are not allowed to participate in this game"), HttpStatus.UNAUTHORIZED);
        }

        if(currentGP.get().getShips().size() > 0) {
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This player already has their ships placed"), HttpStatus.FORBIDDEN);
        }

        if(ships.size() != 5){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must place 5 ships exactly"), HttpStatus.FORBIDDEN);
        }

        for(Ship ship: ships){
            if(ship.getType().equals("patrolboat") && ship.getShipLocations().size() != 2){
                return new ResponseEntity<>(Utils.getDefaultDTO("error", "Ship " + ship.getType() + " must be exactly 2 cells long."), HttpStatus.FORBIDDEN);
            }

            if(ship.getType().equals("submarine") && ship.getShipLocations().size() != 3){
                return new ResponseEntity<>(Utils.getDefaultDTO("error", "Ship " + ship.getType() + " must be exactly 3 cells long."), HttpStatus.FORBIDDEN);
            }

            if(ship.getType().equals("destroyer") && ship.getShipLocations().size() != 3){
                return new ResponseEntity<>(Utils.getDefaultDTO("error", "Ship " + ship.getType() + " must be exactly 3 cells long."), HttpStatus.FORBIDDEN);
            }

            if(ship.getType().equals("battleship") && ship.getShipLocations().size() != 4){
                return new ResponseEntity<>(Utils.getDefaultDTO("error", "Ship " + ship.getType() + " must be exactly 4 cells long."), HttpStatus.FORBIDDEN);
            }

            if(ship.getType().equals("carrier") && ship.getShipLocations().size() != 5){
                return new ResponseEntity<>(Utils.getDefaultDTO("error", "Ship " + ship.getType() + " must be exactly 5 cells long."), HttpStatus.FORBIDDEN);
            }
        }

        for(Ship ship: ships){
            currentGP.get().addShip(ship);
            shipRepository.save(ship);
        }

        return new ResponseEntity<>(Utils.getDefaultDTO("OK", "Ships placed"), HttpStatus.CREATED);
    }

    @RequestMapping(path="games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> shootSalvo(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication){

        Optional<GamePlayer> currentGP = gpRepository.findById(gamePlayerId);
        int currentTurn = 0;

        if(Utils.isGuest(authentication)){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must be logged in to do this"), HttpStatus.UNAUTHORIZED);
        }

        if(currentGP.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This game doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        if(!getCurrentLoggedUser(authentication).getGamePlayers().contains(currentGP.get())){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You are not allowed to participate in this game"), HttpStatus.UNAUTHORIZED);
        }

        Game currentGame = currentGP.get().getGame();
        Optional<GamePlayer> opponent = currentGame.getGamePlayers().stream().filter(gp -> gp != currentGP.get()).findFirst();

        if(opponent.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You don't have an opponent yet"), HttpStatus.FORBIDDEN);
        }

        if(currentGP.get().getSalvoes().size() > opponent.get().getSalvoes().size()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must wait for your opponent to make their shot"), HttpStatus.FORBIDDEN);
        }

        List<Integer> listOfTurns = currentGP.get().getSalvoes().stream().map(s -> s.getTurn()).collect(Collectors.toList());

        if(listOfTurns.size() >= 1){
            currentTurn = listOfTurns.size();
        }

        if(salvo.getSalvoLocations().size() < 1){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You should fire at least once every turn"), HttpStatus.FORBIDDEN);
        }

        if(salvo.getSalvoLocations().size() > 5){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You shouldn't fire more than 5 times every turn"), HttpStatus.FORBIDDEN);
        }

        salvo.setTurn(currentTurn + 1);
        currentGP.get().addSalvo(salvo);
        salvoRepository.save(salvo);

        return new ResponseEntity<>(Utils.getDefaultDTO("OK","Salvo fired!"), HttpStatus.CREATED);
    }

    private Player getCurrentLoggedUser(Authentication authentication){
        return playerRepository.findByUserName(authentication.getName());
    }
}
