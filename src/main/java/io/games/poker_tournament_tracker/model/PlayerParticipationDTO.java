package io.games.poker_tournament_tracker.model;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerParticipationDTO {

  private Integer participationId;

  @NotNull private Boolean participated;

  private OffsetDateTime participationTime;

  @NotNull private Integer game;

  @NotNull private Integer seasonPlayer;
}
