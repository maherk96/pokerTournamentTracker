package io.games.poker_tournament_tracker.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.PlayerParticipation;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;

public interface PlayerParticipationRepository extends JpaRepository<PlayerParticipation, Integer> {

  PlayerParticipation findFirstByGame(Game game);

  PlayerParticipation findFirstBySeasonPlayer(SeasonPlayer seasonPlayer);
}
