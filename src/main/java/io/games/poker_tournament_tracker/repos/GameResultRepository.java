package io.games.poker_tournament_tracker.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.GameResult;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;

public interface GameResultRepository extends JpaRepository<GameResult, Integer> {

  GameResult findFirstByGame(Game game);

  GameResult findFirstBySeasonPlayer(SeasonPlayer seasonPlayer);
}
