package io.games.poker_tournament_tracker.service.impl;

import io.games.poker_tournament_tracker.model.GameBuyInDTO;
import io.games.poker_tournament_tracker.service.GameBuyInService;
import io.games.poker_tournament_tracker.service.GameService;
import io.games.poker_tournament_tracker.service.SeasonPlayerService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameBuyInImpl {

  @Autowired private GameBuyInService gameBuyInService;
  @Autowired private GameService gameService;

  @Autowired private SeasonPlayerService seasonPlayerService;

  public void createGameBuyIn(int gameNumber, String playerName, double buyInAmount) {
    GameBuyInDTO gameBuyInDTO = new GameBuyInDTO();
    gameBuyInDTO.setGame(gameService.getGameId(gameNumber));
    Integer seasonIdByGameNumber = gameService.getSeasonIdByGameNumber(gameNumber);
    gameBuyInDTO.setSeasonPlayer(
        seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
            playerName, seasonIdByGameNumber));
    gameBuyInDTO.setBuyInAmount(BigDecimal.valueOf(buyInAmount));
    gameBuyInService.create(gameBuyInDTO);
  }

  //	•	game_buy_ins
  //
  // Fields to Populate:
  //
  //	•	game_id: Reference to the specific game’s game_id.
  //	•	season_player_id: Reference to the player-season association from season_players.
  //	•	buy_in_amount: The amount the player buys into the game (typically the min_buy_in).
  //	•	buy_in_time: Timestamp when the buy-in was made.
  //
  // Steps:
  //
  //	1.	Insert Records into game_buy_ins for Participating Players:
  //	•	Required Fields: game_id, season_player_id, buy_in_amount.
  //	•	Example Data for Game 1 (All 5 Players Participate):
  //	•	Player A (101): buy_in_amount = 50.00
  //	•	Player B (102): buy_in_amount = 50.00
  //	•	Player C (103): buy_in_amount = 50.00
  //	•	Player D (104): buy_in_amount = 50.00
  //	•	Player E (105): buy_in_amount = 50.00
  //	•	Example Data for Game 2 (Player E Does Not Participate):
  //	•	Players A-D buy in as above.
  //	•	Player E (105): No buy-in record since participated = FALSE.
  //
  // Updating Pot Sizes:
  //
  //	•	Manually Update current_pot_size in season_players:
  //	•	For Each Buy-In:
  //	•	Subtract the buy_in_amount from the player’s current_pot_size.
  //	•	Example Update for Player A (101):
  //	•	Before Buy-In: $1,000.00
  //	•	Buy-In: $50.00
  //	•	After Buy-In: $950.00
  //
  // Constraints:
  //
  //	•	Ensure that each (game_id, season_player_id) pair is unique to comply with the
  // UNIQUE(game_id, season_player_id) constraint.
}
