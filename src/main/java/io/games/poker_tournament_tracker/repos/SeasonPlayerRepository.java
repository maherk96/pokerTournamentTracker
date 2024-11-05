package io.games.poker_tournament_tracker.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.games.poker_tournament_tracker.domain.Player;
import io.games.poker_tournament_tracker.domain.Season;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;

public interface SeasonPlayerRepository extends JpaRepository<SeasonPlayer, Integer> {

  SeasonPlayer findFirstBySeason(Season season);

  SeasonPlayer findFirstByPlayer(Player player);

  Optional<SeasonPlayer> findByPlayerAndSeason(Player player, Season season);
}
