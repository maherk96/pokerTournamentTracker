package io.games.poker_tournament_tracker.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GameBuyIn {

  @Id
  @Column(nullable = false, updatable = false)
  @SequenceGenerator(
      name = "primary_sequence",
      sequenceName = "primary_sequence",
      allocationSize = 1,
      initialValue = 10000)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
  private Integer gameBuyInId;

  @Column(nullable = false, precision = 14, scale = 2)
  private BigDecimal buyInAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  private Game game;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "season_player_id", nullable = false)
  private SeasonPlayer seasonPlayer;
}
