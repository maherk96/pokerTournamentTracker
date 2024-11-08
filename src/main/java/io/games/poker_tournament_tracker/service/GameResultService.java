package io.games.poker_tournament_tracker.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.games.poker_tournament_tracker.domain.GameResult;
import io.games.poker_tournament_tracker.model.GameResultDTO;
import io.games.poker_tournament_tracker.repos.GameRepository;
import io.games.poker_tournament_tracker.repos.GameResultRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/** Service class for managing Game Results. */
@Service
@Slf4j
public class GameResultService {

  private final GameResultRepository gameResultRepository;
  private final GameRepository gameRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;
  private final GameService gameService;
  private final SeasonPlayerService seasonPlayerService;
  private final GameBuyInService gameBuyInService;

  @Autowired
  public GameResultService(
      GameResultRepository gameResultRepository,
      GameRepository gameRepository,
      SeasonPlayerRepository seasonPlayerRepository,
      GameService gameService,
      SeasonPlayerService seasonPlayerService,
      GameBuyInService gameBuyInService) {
    this.gameResultRepository = gameResultRepository;
    this.gameRepository = gameRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.gameService = gameService;
    this.seasonPlayerService = seasonPlayerService;
    this.gameBuyInService = gameBuyInService;
  }

  /**
   * Retrieves all GameResultDTOs.
   *
   * @return a list of GameResultDTOs
   */
  public List<GameResultDTO> findAll() {
    log.info("Retrieving all game results");
    List<GameResult> gameResults = gameResultRepository.findAll(Sort.by("gameResultId"));
    return gameResults.stream().map(this::mapToDTO).toList();
  }

  /**
   * Retrieves a GameResultDTO by its ID.
   *
   * @param gameResultId the ID of the GameResult
   * @return the GameResultDTO
   */
  public GameResultDTO get(final Integer gameResultId) {
    log.info("Retrieving game result with id: {}", gameResultId);
    return gameResultRepository
        .findById(gameResultId)
        .map(this::mapToDTO)
        .orElseThrow(() -> new NotFoundException("Game result not found"));
  }

  /**
   * Creates a new GameResult.
   *
   * @param gameResultDTO the DTO of the GameResult to create
   * @return the ID of the created GameResult
   */
  @Transactional
  public Integer create(final GameResultDTO gameResultDTO) {
    log.info("Creating new game result");
    GameResult gameResult = new GameResult();
    mapToEntity(gameResultDTO, gameResult);
    return gameResultRepository.save(gameResult).getGameResultId();
  }

  /**
   * Updates an existing GameResult.
   *
   * @param gameResultId the ID of the GameResult to update
   * @param gameResultDTO the DTO of the GameResult to update
   */
  @Transactional
  public void update(final Integer gameResultId, final GameResultDTO gameResultDTO) {
    log.info("Updating game result with id: {}", gameResultId);
    GameResult gameResult =
        gameResultRepository
            .findById(gameResultId)
            .orElseThrow(() -> new NotFoundException("Game result not found"));
    mapToEntity(gameResultDTO, gameResult);
    gameResultRepository.save(gameResult);
  }

  /**
   * Deletes a GameResult by its ID.
   *
   * @param gameResultId the ID of the GameResult to delete
   */
  @Transactional
  public void delete(final Integer gameResultId) {
    log.info("Deleting game result with id: {}", gameResultId);
    gameResultRepository.deleteById(gameResultId);
  }

  /**
   * Maps a GameResult entity to a GameResultDTO.
   *
   * @param gameResult the GameResult entity
   * @return the mapped GameResultDTO
   */
  private GameResultDTO mapToDTO(final GameResult gameResult) {
    GameResultDTO gameResultDTO = new GameResultDTO();
    gameResultDTO.setGameResultId(gameResult.getGameResultId());
    gameResultDTO.setWinnings(gameResult.getWinnings());
    gameResultDTO.setGame(gameResult.getGame() == null ? null : gameResult.getGame().getGameId());
    gameResultDTO.setSeasonPlayer(
        gameResult.getSeasonPlayer() == null
            ? null
            : gameResult.getSeasonPlayer().getSeasonPlayerId());
    return gameResultDTO;
  }

  /**
   * Maps a GameResultDTO to a GameResult entity.
   *
   * @param gameResultDTO the GameResultDTO
   * @param gameResult the GameResult entity
   */
  private void mapToEntity(final GameResultDTO gameResultDTO, final GameResult gameResult) {
    gameResult.setWinnings(gameResultDTO.getWinnings());
    gameResult.setGame(
        gameResultDTO.getGame() == null
            ? null
            : gameRepository
                .findById(gameResultDTO.getGame())
                .orElseThrow(() -> new NotFoundException("Game not found")));
    gameResult.setSeasonPlayer(
        gameResultDTO.getSeasonPlayer() == null
            ? null
            : seasonPlayerRepository
                .findById(gameResultDTO.getSeasonPlayer())
                .orElseThrow(() -> new NotFoundException("Season player not found")));
  }

  //  public BigDecimal getPlayerCurrentPotSize(String playerName, int gameNumber) {
  //    SeasonDTO seasonPlayerForGame = seasonPlayerService.getSeasonPlayerForGame(gameNumber);
  //    seasonPlayerForGame.get
  //    var seasonId = gameService.getSeasonIdByGameNumber(gameNumber);
  //    Integer seasonPlayerIdByPlayerNameAndSeasonId =
  // seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(playerName, seasonId);
  //    SeasonPlayerDTO seasonPlayerDTO =
  // seasonPlayerService.get(seasonPlayerIdByPlayerNameAndSeasonId);
  //    return seasonPlayerDTO.getCurrentPotSize();
  //  }

  /**
   * Creates a new GameResult for a specific game and player.
   *
   * @param gameNumber the number of the game
   * @param playerName the name of the player
   * @param winnings the amount of the winnings
   */
  @Transactional
  public void createGameResult(int gameNumber, String playerName, double winnings) {
    try {
      //      var initialBuyIn = gameBuyInService.getTotalBuyInAmountByGameNumber(playerName,
      // gameNumber);
      //      var currentPotSize = getPlayerCurrentPotSize(playerName, gameNumber);
      //
      //      if (initialBuyIn.isPresent()) {
      //        var buyIn = initialBuyIn.get();
      //        if (buyIn.getBuyInAmount().compareTo(BigDecimal.valueOf(winnings)) >= 0) {
      //
      //          currentPotSize = currentPotSize.subtract(BigDecimal.valueOf(winnings));
      //        }
      //
      //        }

      log.info("Creating game result for game number: {}, player name: {}", gameNumber, playerName);
      GameResultDTO gameResultDTO = new GameResultDTO();
      gameResultDTO.setGame(gameService.getGameId(gameNumber));
      Integer seasonIdByGameNumber = gameService.getSeasonIdByGameNumber(gameNumber);
      gameResultDTO.setSeasonPlayer(
          seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
              playerName, seasonIdByGameNumber));
      gameResultDTO.setWinnings(BigDecimal.valueOf(winnings));
      create(gameResultDTO);
    } catch (Exception e) {
      log.error(
          "Error creating game result for game number: {}, player name: {}",
          gameNumber,
          playerName,
          e);
      throw new RuntimeException("Error creating game result", e);
    }
  }
}
