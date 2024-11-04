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
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SeasonPlayer {

  @Id
  @Column(nullable = false, updatable = false)
  @SequenceGenerator(
      name = "primary_sequence",
      sequenceName = "primary_sequence",
      allocationSize = 1,
      initialValue = 10000)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
  private Integer seasonPlayerId;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal allocatedPotSize;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal minBuyIn;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal currentPotSize;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", nullable = false)
  private Season season;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "player_id", nullable = false)
  private Player player;

  @OneToMany(mappedBy = "seasonPlayer")
  private Set<GameBuyIn> seasonPlayerGameBuyIns;

  @OneToMany(mappedBy = "seasonPlayer")
  private Set<GameResult> seasonPlayerGameResults;

  @OneToMany(mappedBy = "seasonPlayer")
  private Set<PlayerParticipation> seasonPlayerPlayerParticipations;
}
