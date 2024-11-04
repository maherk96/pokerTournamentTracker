package io.games.poker_tournament_tracker.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Game {

  @Id
  @Column(nullable = false, updatable = false)
  @SequenceGenerator(
      name = "primary_sequence",
      sequenceName = "primary_sequence",
      allocationSize = 1,
      initialValue = 10000)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
  private Integer gameId;

  @Column(nullable = false)
  private Integer gameNumber;

  @Column private OffsetDateTime startTime;

  @Column private OffsetDateTime endTime;

  @Column private OffsetDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", nullable = false)
  private Season season;

  @OneToMany(mappedBy = "game")
  private Set<GameBuyIn> gameGameBuyIns;

  @OneToMany(mappedBy = "game")
  private Set<GameResult> gameGameResults;

  @OneToMany(mappedBy = "game")
  private Set<PlayerParticipation> gamePlayerParticipations;
}
