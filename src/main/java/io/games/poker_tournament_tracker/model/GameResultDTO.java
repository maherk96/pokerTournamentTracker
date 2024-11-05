package io.games.poker_tournament_tracker.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResultDTO {

  private Integer gameResultId;

  @NotNull
  @Digits(integer = 14, fraction = 2)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(type = "string", example = "49.08")
  private BigDecimal winnings;

  @NotNull private Integer game;

  @NotNull private Integer seasonPlayer;
}
