package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlayersController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestParam String email, @RequestParam String password){
        if(email.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must enter an email"), HttpStatus.FORBIDDEN);
        }

        if(password.isEmpty()){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "You must enter a password"), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByUserName(email);
        if(player != null){
            return new ResponseEntity<>(Utils.getDefaultDTO("error", "This mail is already in use"), HttpStatus.CONFLICT);
        }

        Player newPlayer = new Player(email, passwordEncoder.encode(password));
        playerRepository.save(newPlayer);

        return new ResponseEntity<>(Utils.getDefaultDTO("username", newPlayer.getUserName()), HttpStatus.CREATED);
    }
}
