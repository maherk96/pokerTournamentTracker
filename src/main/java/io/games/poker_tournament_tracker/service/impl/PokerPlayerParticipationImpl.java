package io.games.poker_tournament_tracker.service.impl;

import io.games.poker_tournament_tracker.model.PlayerParticipationDTO;
import io.games.poker_tournament_tracker.service.GameService;
import io.games.poker_tournament_tracker.service.PlayerParticipationService;
import io.games.poker_tournament_tracker.service.PlayerService;
import io.games.poker_tournament_tracker.service.SeasonPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PokerPlayerParticipationImpl {

  @Autowired private PlayerParticipationService playerParticipationService;

  @Autowired private PlayerService playerService;

  @Autowired private SeasonPlayerService seasonPlayerService;

  @Autowired private GameService gameService;

  public void createPlayerParticipation(
      String playerName, PlayerParticipation playerParticipation, int gameNumber) {
    PlayerParticipationDTO playerParticipationDTO = new PlayerParticipationDTO();
    playerParticipationDTO.setSeasonPlayer(
        seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
            playerName, gameService.getSeasonIdByGameNumber(gameNumber)));
    playerParticipationDTO.setGame(gameService.getGameId(gameNumber));
    playerParticipationDTO.setParticipated(playerParticipation.equals(PlayerParticipation.YES));
    playerParticipationService.create(playerParticipationDTO);
  }
}
