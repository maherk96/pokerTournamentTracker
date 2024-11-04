package io.games.poker_tournament_tracker.repos;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.PlayerParticipation;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerParticipationRepository extends JpaRepository<PlayerParticipation, Integer> {

  PlayerParticipation findFirstByGame(Game game);

  PlayerParticipation findFirstBySeasonPlayer(SeasonPlayer seasonPlayer);
}
