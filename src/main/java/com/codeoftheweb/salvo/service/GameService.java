package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.dtos.GameDTO;
import com.codeoftheweb.salvo.dtos.GameViewDTO;
import com.codeoftheweb.salvo.dtos.PlayerDTO;
import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;

public interface GameService {

    GameDTO makeGameDTO(Game game);

    GameViewDTO makeGameViewDTO(Game game, GamePlayer gamePlayer);

    PlayerDTO makePlayerDTO(Player player);
}
