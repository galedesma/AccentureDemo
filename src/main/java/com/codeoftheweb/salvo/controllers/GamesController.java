package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import com.codeoftheweb.salvo.service.GameService;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class GamesController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gpRepository;

    @Autowired
    private GameService gameService;

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> getAllGames(Authentication authentication){
        Map<String, Object> dto = new LinkedHashMap<>();
        List<Object> auxList = new ArrayList<>();
        List<Game> games = gameRepository.findAll();

        for(Game game: games){
            auxList.add(gameService.makeGameDTO(game));
        }

        if(Utils.isGuest(authentication)){
            dto.put("player", "Guest");
        } else {
            dto.put("player", gameService.makePlayerDTO(getCurrentLoggedUser(authentication)));
        }

        dto.put("games", auxList);
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
        if(Utils.isGuest(authentication)){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must be logged in to create a game"), HttpStatus.UNAUTHORIZED);
        }

        Game newGame = new Game(new Date());
        gameRepository.save(newGame);

        GamePlayer newGP = new GamePlayer(newGame, getCurrentLoggedUser(authentication));
        gpRepository.save(newGP);
        return new ResponseEntity<>(Utils.getDefaultDTO("gpid", newGP.getId()), HttpStatus.CREATED);
    }

    private Player getCurrentLoggedUser(Authentication authentication){
        return playerRepository.findByUserName(authentication.getName());
    }
}
