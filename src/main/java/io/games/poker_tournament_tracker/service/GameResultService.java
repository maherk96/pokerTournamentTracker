package io.games.poker_tournament_tracker.service;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.GameResult;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.GameResultDTO;
import io.games.poker_tournament_tracker.repos.GameRepository;
import io.games.poker_tournament_tracker.repos.GameResultRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing Game Results. */
@Service
public class GameResultService {

  private static final Logger logger = LoggerFactory.getLogger(GameResultService.class);

  @Autowired private GameResultRepository gameResultRepository;
  @Autowired private GameRepository gameRepository;
  @Autowired private SeasonPlayerRepository seasonPlayerRepository;
  @Autowired private GameService gameService;
  @Autowired private SeasonPlayerService seasonPlayerService;
  @Autowired private GameResultService gameResultService;

  /**
   * Retrieves all GameResultDTOs.
   *
   * @return a list of GameResultDTOs
   */
  public List<GameResultDTO> findAll() {
    try {
      logger.info("Retrieving all game results");
      final List<GameResult> gameResults = gameResultRepository.findAll(Sort.by("gameResultId"));
      return gameResults.stream()
          .map(gameResult -> mapToDTO(gameResult, new GameResultDTO()))
          .toList();
    } catch (Exception e) {
      logger.error("Error finding all game results", e);
      throw e;
    }
  }

  /**
   * Retrieves a GameResultDTO by its ID.
   *
   * @param gameResultId the ID of the GameResult
   * @return the GameResultDTO
   */
  public GameResultDTO get(final Integer gameResultId) {
    try {
      logger.info("Retrieving game result with id: {}", gameResultId);
      return gameResultRepository
          .findById(gameResultId)
          .map(gameResult -> mapToDTO(gameResult, new GameResultDTO()))
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      logger.warn("Game result not found with id: {}", gameResultId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error getting game result with id: {}", gameResultId, e);
      throw e;
    }
  }

  /**
   * Creates a new GameResult.
   *
   * @param gameResultDTO the DTO of the GameResult to create
   * @return the ID of the created GameResult
   */
  @Transactional
  public Integer create(final GameResultDTO gameResultDTO) {
    try {
      logger.info("Creating new game result");
      final GameResult gameResult = new GameResult();
      mapToEntity(gameResultDTO, gameResult);
      return gameResultRepository.save(gameResult).getGameResultId();
    } catch (Exception e) {
      logger.error("Error creating game result", e);
      throw e;
    }
  }

  /**
   * Updates an existing GameResult.
   *
   * @param gameResultId the ID of the GameResult to update
   * @param gameResultDTO the DTO of the GameResult to update
   */
  @Transactional
  public void update(final Integer gameResultId, final GameResultDTO gameResultDTO) {
    try {
      logger.info("Updating game result with id: {}", gameResultId);
      final GameResult gameResult =
          gameResultRepository.findById(gameResultId).orElseThrow(NotFoundException::new);
      mapToEntity(gameResultDTO, gameResult);
      gameResultRepository.save(gameResult);
    } catch (NotFoundException e) {
      logger.warn("Game result not found with id: {}", gameResultId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error updating game result with id: {}", gameResultId, e);
      throw e;
    }
  }

  /**
   * Deletes a GameResult by its ID.
   *
   * @param gameResultId the ID of the GameResult to delete
   */
  @Transactional
  public void delete(final Integer gameResultId) {
    try {
      logger.info("Deleting game result with id: {}", gameResultId);
      gameResultRepository.deleteById(gameResultId);
    } catch (Exception e) {
      logger.error("Error deleting game result with id: {}", gameResultId, e);
      throw e;
    }
  }

  /**
   * Maps a GameResult entity to a GameResultDTO.
   *
   * @param gameResult the GameResult entity
   * @param gameResultDTO the GameResultDTO
   * @return the mapped GameResultDTO
   */
  private GameResultDTO mapToDTO(final GameResult gameResult, final GameResultDTO gameResultDTO) {
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
   * @return the mapped GameResult entity
   */
  private GameResult mapToEntity(final GameResultDTO gameResultDTO, final GameResult gameResult) {
    gameResult.setWinnings(gameResultDTO.getWinnings());
    final Game game =
        gameResultDTO.getGame() == null
            ? null
            : gameRepository
                .findById(gameResultDTO.getGame())
                .orElseThrow(() -> new NotFoundException("game not found"));
    gameResult.setGame(game);
    final SeasonPlayer seasonPlayer =
        gameResultDTO.getSeasonPlayer() == null
            ? null
            : seasonPlayerRepository
                .findById(gameResultDTO.getSeasonPlayer())
                .orElseThrow(() -> new NotFoundException("seasonPlayer not found"));
    gameResult.setSeasonPlayer(seasonPlayer);
    return gameResult;
  }

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
      logger.info(
          "Creating game result for game number: {}, player name: {}", gameNumber, playerName);
      GameResultDTO gameResultDTO = new GameResultDTO();
      gameResultDTO.setGame(gameService.getGameId(gameNumber));
      Integer seasonIdByGameNumber = gameService.getSeasonIdByGameNumber(gameNumber);
      gameResultDTO.setSeasonPlayer(
          seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
              playerName, seasonIdByGameNumber));
      gameResultDTO.setWinnings(BigDecimal.valueOf(winnings));
      gameResultService.create(gameResultDTO);
    } catch (Exception e) {
      logger.error(
          "Error creating game result for game number: {}, player name: {}",
          gameNumber,
          playerName,
          e);
      throw e;
    }
  }
}
