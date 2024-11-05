package io.games.poker_tournament_tracker.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeasonDTO {

  private Integer seasonId;

  @NotNull
  @Size(max = 100)
  private String name;

  @NotNull private LocalDate startDate;

  private LocalDate endDate;

  private OffsetDateTime createdAt;
}
