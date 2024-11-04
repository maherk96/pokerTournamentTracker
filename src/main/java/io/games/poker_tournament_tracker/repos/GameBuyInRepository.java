package io.games.poker_tournament_tracker.repos;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.GameBuyIn;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameBuyInRepository extends JpaRepository<GameBuyIn, Integer> {

  GameBuyIn findFirstByGame(Game game);

  GameBuyIn findFirstBySeasonPlayer(SeasonPlayer seasonPlayer);
}
