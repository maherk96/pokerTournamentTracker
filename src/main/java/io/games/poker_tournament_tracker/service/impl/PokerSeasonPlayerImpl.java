package io.games.poker_tournament_tracker.service.impl;

import io.games.poker_tournament_tracker.model.SeasonPlayerDTO;
import io.games.poker_tournament_tracker.service.PlayerService;
import io.games.poker_tournament_tracker.service.SeasonPlayerService;
import io.games.poker_tournament_tracker.service.SeasonService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PokerSeasonPlayerImpl {

  @Autowired private SeasonPlayerService seasonPlayerService;

  @Autowired private SeasonService seasonService;

  @Autowired private PlayerService playerService;

  public void createSeasonPlayers(
      String seasonName, String playerName, Double minBuyIn, Double allocatedPotSize) {
    SeasonPlayerDTO seasonPlayerDTO = new SeasonPlayerDTO();
    seasonPlayerDTO.setSeason(seasonService.getSeasonIdByName(seasonName));
    Integer playerId = playerService.getOrCreatePlayerIdByName(playerName);
    if (playerId == null) {
      throw new IllegalStateException("Player ID is null for player: " + playerName);
    }
    seasonPlayerDTO.setPlayer(playerId);
    seasonPlayerDTO.setMinBuyIn(BigDecimal.valueOf(minBuyIn));
    seasonPlayerDTO.setAllocatedPotSize(BigDecimal.valueOf(allocatedPotSize));
    seasonPlayerDTO.setCurrentPotSize(BigDecimal.valueOf(allocatedPotSize));
    System.out.println(seasonPlayerDTO);
    seasonPlayerService.create(seasonPlayerDTO);
  }
}
