package io.games.poker_tournament_tracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PlayerParticipation {

  @Id
  @Column(nullable = false, updatable = false)
  @SequenceGenerator(
      name = "primary_sequence",
      sequenceName = "primary_sequence",
      allocationSize = 1,
      initialValue = 10000)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
  private Integer participationId;

  @Column(nullable = false)
  private Boolean participated;

  @Column private OffsetDateTime participationTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_player_id", nullable = false)
  private SeasonPlayer seasonPlayer;
}