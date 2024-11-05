package io.games.poker_tournament_tracker.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SeasonPlayerDTO {

  private Integer seasonPlayerId;

  @NotNull
  @Digits(integer = 14, fraction = 2)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(type = "string", example = "21.08")
  private BigDecimal allocatedPotSize;

  @NotNull
  @Digits(integer = 14, fraction = 2)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(type = "string", example = "11.08")
  private BigDecimal minBuyIn;

  @NotNull
  @Digits(integer = 14, fraction = 2)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(type = "string", example = "77.08")
  private BigDecimal currentPotSize;

  @NotNull private Integer season;

  @NotNull private Integer player;
}
