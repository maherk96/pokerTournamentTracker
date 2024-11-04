package io.games.poker_tournament_tracker.service.impl;

import io.games.poker_tournament_tracker.model.GameDTO;
import io.games.poker_tournament_tracker.service.GameService;
import io.games.poker_tournament_tracker.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PokerGamesImpl {

  @Autowired private GameService gameService;

  @Autowired private SeasonService seasonService;

  public void createGame(String seasonName, int gameNumber) {
    GameDTO gamesDTO = new GameDTO();
    gamesDTO.setSeason(seasonService.getSeasonIdByName(seasonName));
    gamesDTO.setGameNumber(gameNumber);
    gameService.create(gamesDTO);
  }
}
