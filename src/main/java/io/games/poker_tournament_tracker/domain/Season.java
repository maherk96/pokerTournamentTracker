package io.games.poker_tournament_tracker.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Season {

  @Id
  @Column(nullable = false, updatable = false)
  @SequenceGenerator(
      name = "primary_sequence",
      sequenceName = "primary_sequence",
      allocationSize = 1,
      initialValue = 10000)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_sequence")
  private Integer seasonId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column private LocalDate endDate;

  @Column private OffsetDateTime createdAt;

  @OneToMany(mappedBy = "season")
  private Set<SeasonPlayer> seasonSeasonPlayers;

  @OneToMany(mappedBy = "season")
  private Set<Game> seasonGames;
}
