package io.games.poker_tournament_tracker.model;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDTO {

  private Integer gameId;

  @NotNull private Integer gameNumber;

  private OffsetDateTime startTime;

  private OffsetDateTime endTime;

  private OffsetDateTime createdAt;

  @NotNull private Integer season;
}
