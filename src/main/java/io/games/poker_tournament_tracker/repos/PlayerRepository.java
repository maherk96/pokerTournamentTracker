package io.games.poker_tournament_tracker.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.games.poker_tournament_tracker.domain.Player;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

  @Query("SELECT p.playerId FROM Player p WHERE p.name = :name")
  int findPlayerIdByName(@Param("name") String name);

  Optional<Player> findByName(String name);
}
