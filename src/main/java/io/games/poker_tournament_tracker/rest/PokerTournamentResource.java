package io.games.poker_tournament_tracker.rest;

import io.games.poker_tournament_tracker.service.*;
import io.games.poker_tournament_tracker.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/poker/tournament", produces = MediaType.APPLICATION_JSON_VALUE)
public class PokerTournamentResource {

  @Autowired private SeasonService seasonService;

  @Autowired private SeasonPlayerService seasonPlayerService;

  @Autowired private GameService gameService;

  @Autowired private PlayerParticipationService playerParticipationService;

  @Autowired GameBuyInService gameBuyInService;

  @Autowired GameResultService gameResultService;

  @PostMapping("/create-season")
  public ResponseEntity<Void> createSeason(@RequestParam String seasonName) {
    seasonService.createSeason(seasonName);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/create-season-player")
  public ResponseEntity<Void> createSeasonPlayers(
      @RequestParam String seasonName,
      @RequestParam String playerName,
      @RequestParam Double minBuyIn,
      @RequestParam Double allocatedPotSize) {
    seasonPlayerService.createSeasonPlayers(seasonName, playerName, minBuyIn, allocatedPotSize);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/games")
  public ResponseEntity<Void> createGame(
      @RequestParam String seasonName, @RequestParam int gameNumber) {
    gameService.createGame(seasonName, gameNumber);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/player-participation")
  public ResponseEntity<Void> createPlayerParticipation(
      @RequestParam String playerName,
      @RequestParam PlayerParticipation playerParticipation,
      @RequestParam int gameNumber) {
    playerParticipationService.createPlayerParticipation(
        playerName, playerParticipation, gameNumber);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/create-game-buy-in")
  public ResponseEntity<Void> createGameBuyIn(
      @RequestParam int gameNumber,
      @RequestParam String playerName,
      @RequestParam double buyInAmount) {
    gameBuyInService.createGameBuyIn(gameNumber, playerName, buyInAmount);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/create-game-result")
  public ResponseEntity<Void> createGameResult(
      @RequestParam int gameNumber,
      @RequestParam String playerName,
      @RequestParam double winnings) {
    gameResultService.createGameResult(gameNumber, playerName, winnings);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
