package io.games.poker_tournament_tracker.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.games.poker_tournament_tracker.domain.Season;

public interface SeasonRepository extends JpaRepository<Season, Integer> {

  @Query("SELECT s.seasonId FROM Season s WHERE s.name = :name")
  int findSeasonIdByName(@Param("name") String name);

  Optional<Season> findByName(String name);
}
