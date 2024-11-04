package io.games.poker_tournament_tracker.repos;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.Season;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameRepository extends JpaRepository<Game, Integer> {

  Game findFirstBySeason(Season season);

  @Query("SELECT g.gameId FROM Game g WHERE g.gameNumber = :gameNumber")
  int findGameIdByGameNumber(int gameNumber);

  Optional<Game> findByGameNumber(int gameNumber);
}
