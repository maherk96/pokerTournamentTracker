package io.games.poker_tournament_tracker.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDTO {

  private Integer playerId;

  @NotNull
  @Size(max = 100)
  private String name;

  private OffsetDateTime createdAt;
}