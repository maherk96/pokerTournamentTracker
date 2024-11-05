package io.games.poker_tournament_tracker.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.GameBuyIn;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;

public interface GameBuyInRepository extends JpaRepository<GameBuyIn, Integer> {

  GameBuyIn findFirstByGame(Game game);

  GameBuyIn findFirstBySeasonPlayer(SeasonPlayer seasonPlayer);
}
