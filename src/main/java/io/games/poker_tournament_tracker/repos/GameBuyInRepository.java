package io.games.poker_tournament_tracker.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.GameBuyIn;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;

public interface GameBuyInRepository extends JpaRepository<GameBuyIn, Integer> {

  GameBuyIn findFirstByGame(Game game);

  GameBuyIn findFirstBySeasonPlayer(SeasonPlayer seasonPlayer);

  @Query(
      "SELECT gbi FROM GameBuyIn gbi "
          + "JOIN gbi.game g "
          + "JOIN g.season s "
          + "JOIN gbi.seasonPlayer sp "
          + "JOIN sp.player p "
          + "WHERE p.name = :playerName AND g.gameNumber = :gameNumber")
  Optional<GameBuyIn> findGameBuyInByPlayerNameAndGameNumber(
      @Param("playerName") String playerName, @Param("gameNumber") int gameNumber);
}
