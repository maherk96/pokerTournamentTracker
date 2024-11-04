package io.games.poker_tournament_tracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameBuyInDTO {

  private Integer gameBuyInId;

  @NotNull
  @Digits(integer = 14, fraction = 2)
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(type = "string", example = "29.08")
  private BigDecimal buyInAmount;

  @NotNull private Integer game;

  @NotNull private Integer seasonPlayer;
}
