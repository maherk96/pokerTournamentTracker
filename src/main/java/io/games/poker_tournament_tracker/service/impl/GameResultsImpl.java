package io.games.poker_tournament_tracker.service.impl;

import io.games.poker_tournament_tracker.model.GameResultDTO;
import io.games.poker_tournament_tracker.service.GameResultService;
import io.games.poker_tournament_tracker.service.GameService;
import io.games.poker_tournament_tracker.service.SeasonPlayerService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameResultsImpl {

  @Autowired private GameResultService gameResultService;
  @Autowired private GameService gameService;

  @Autowired private SeasonPlayerService seasonPlayerService;

  public void createGameResult(int gameNumber, String playerName, double winnings) {
    GameResultDTO gameResultDTO = new GameResultDTO();
    gameResultDTO.setGame(gameService.getGameId(gameNumber));
    Integer seasonIdByGameNumber = gameService.getSeasonIdByGameNumber(gameNumber);
    gameResultDTO.setSeasonPlayer(
        seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
            playerName, seasonIdByGameNumber));
    gameResultDTO.setWinnings(BigDecimal.valueOf(winnings));
    gameResultService.create(gameResultDTO);
  }
}
