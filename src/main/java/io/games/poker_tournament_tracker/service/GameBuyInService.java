package io.games.poker_tournament_tracker.service;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.GameBuyIn;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.GameBuyInDTO;
import io.games.poker_tournament_tracker.repos.GameBuyInRepository;
import io.games.poker_tournament_tracker.repos.GameRepository;
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

/** Service class for managing Game Buy-Ins. */
@Service
public class GameBuyInService {

  private static final Logger logger = LoggerFactory.getLogger(GameBuyInService.class);

  @Autowired private GameBuyInRepository gameBuyInRepository;
  @Autowired private GameRepository gameRepository;
  @Autowired private SeasonPlayerRepository seasonPlayerRepository;
  @Autowired private GameBuyInService gameBuyInService;
  @Autowired private GameService gameService;
  @Autowired private SeasonPlayerService seasonPlayerService;

  /**
   * Retrieves all GameBuyInDTOs.
   *
   * @return a list of GameBuyInDTOs
   */
  public List<GameBuyInDTO> findAll() {
    try {
      logger.info("Retrieving all game buy-ins");
      final List<GameBuyIn> gameBuyIns = gameBuyInRepository.findAll(Sort.by("gameBuyInId"));
      return gameBuyIns.stream().map(gameBuyIn -> mapToDTO(gameBuyIn, new GameBuyInDTO())).toList();
    } catch (Exception e) {
      logger.error("Error finding all game buy-ins", e);
      throw e;
    }
  }

  /**
   * Retrieves a GameBuyInDTO by its ID.
   *
   * @param gameBuyInId the ID of the GameBuyIn
   * @return the GameBuyInDTO
   */
  public GameBuyInDTO get(final Integer gameBuyInId) {
    try {
      logger.info("Retrieving game buy-in with id: {}", gameBuyInId);
      return gameBuyInRepository
          .findById(gameBuyInId)
          .map(gameBuyIn -> mapToDTO(gameBuyIn, new GameBuyInDTO()))
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      logger.warn("Game buy-in not found with id: {}", gameBuyInId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error getting game buy-in with id: {}", gameBuyInId, e);
      throw e;
    }
  }

  /**
   * Creates a new GameBuyIn.
   *
   * @param gameBuyInDTO the DTO of the GameBuyIn to create
   * @return the ID of the created GameBuyIn
   */
  @Transactional
  public Integer create(final GameBuyInDTO gameBuyInDTO) {
    try {
      logger.info("Creating new game buy-in");
      final GameBuyIn gameBuyIn = new GameBuyIn();
      mapToEntity(gameBuyInDTO, gameBuyIn);
      return gameBuyInRepository.save(gameBuyIn).getGameBuyInId();
    } catch (Exception e) {
      logger.error("Error creating game buy-in", e);
      throw e;
    }
  }

  /**
   * Updates an existing GameBuyIn.
   *
   * @param gameBuyInId the ID of the GameBuyIn to update
   * @param gameBuyInDTO the DTO of the GameBuyIn to update
   */
  @Transactional
  public void update(final Integer gameBuyInId, final GameBuyInDTO gameBuyInDTO) {
    try {
      logger.info("Updating game buy-in with id: {}", gameBuyInId);
      final GameBuyIn gameBuyIn =
          gameBuyInRepository.findById(gameBuyInId).orElseThrow(NotFoundException::new);
      mapToEntity(gameBuyInDTO, gameBuyIn);
      gameBuyInRepository.save(gameBuyIn);
    } catch (NotFoundException e) {
      logger.warn("Game buy-in not found with id: {}", gameBuyInId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error updating game buy-in with id: {}", gameBuyInId, e);
      throw e;
    }
  }

  /**
   * Deletes a GameBuyIn by its ID.
   *
   * @param gameBuyInId the ID of the GameBuyIn to delete
   */
  @Transactional
  public void delete(final Integer gameBuyInId) {
    try {
      logger.info("Deleting game buy-in with id: {}", gameBuyInId);
      gameBuyInRepository.deleteById(gameBuyInId);
    } catch (Exception e) {
      logger.error("Error deleting game buy-in with id: {}", gameBuyInId, e);
      throw e;
    }
  }

  /**
   * Maps a GameBuyIn entity to a GameBuyInDTO.
   *
   * @param gameBuyIn the GameBuyIn entity
   * @param gameBuyInDTO the GameBuyInDTO
   * @return the mapped GameBuyInDTO
   */
  private GameBuyInDTO mapToDTO(final GameBuyIn gameBuyIn, final GameBuyInDTO gameBuyInDTO) {
    gameBuyInDTO.setGameBuyInId(gameBuyIn.getGameBuyInId());
    gameBuyInDTO.setBuyInAmount(gameBuyIn.getBuyInAmount());
    gameBuyInDTO.setGame(gameBuyIn.getGame() == null ? null : gameBuyIn.getGame().getGameId());
    gameBuyInDTO.setSeasonPlayer(
        gameBuyIn.getSeasonPlayer() == null
            ? null
            : gameBuyIn.getSeasonPlayer().getSeasonPlayerId());
    return gameBuyInDTO;
  }

  /**
   * Maps a GameBuyInDTO to a GameBuyIn entity.
   *
   * @param gameBuyInDTO the GameBuyInDTO
   * @param gameBuyIn the GameBuyIn entity
   * @return the mapped GameBuyIn entity
   */
  private GameBuyIn mapToEntity(final GameBuyInDTO gameBuyInDTO, final GameBuyIn gameBuyIn) {
    gameBuyIn.setBuyInAmount(gameBuyInDTO.getBuyInAmount());
    final Game game =
        gameBuyInDTO.getGame() == null
            ? null
            : gameRepository
                .findById(gameBuyInDTO.getGame())
                .orElseThrow(() -> new NotFoundException("game not found"));
    gameBuyIn.setGame(game);
    final SeasonPlayer seasonPlayer =
        gameBuyInDTO.getSeasonPlayer() == null
            ? null
            : seasonPlayerRepository
                .findById(gameBuyInDTO.getSeasonPlayer())
                .orElseThrow(() -> new NotFoundException("seasonPlayer not found"));
    gameBuyIn.setSeasonPlayer(seasonPlayer);
    return gameBuyIn;
  }

  /**
   * Creates a new GameBuyIn for a specific game and player.
   *
   * @param gameNumber the number of the game
   * @param playerName the name of the player
   * @param buyInAmount the amount of the buy-in
   */
  @Transactional
  public void createGameBuyIn(int gameNumber, String playerName, double buyInAmount) {
    try {
      logger.info(
          "Creating game buy-in for game number: {}, player name: {}", gameNumber, playerName);
      GameBuyInDTO gameBuyInDTO = new GameBuyInDTO();
      gameBuyInDTO.setGame(gameService.getGameId(gameNumber));
      Integer seasonIdByGameNumber = gameService.getSeasonIdByGameNumber(gameNumber);
      gameBuyInDTO.setSeasonPlayer(
          seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
              playerName, seasonIdByGameNumber));
      gameBuyInDTO.setBuyInAmount(BigDecimal.valueOf(buyInAmount));
      gameBuyInService.create(gameBuyInDTO);
    } catch (Exception e) {
      logger.error(
          "Error creating game buy-in for game number: {}, player name: {}",
          gameNumber,
          playerName,
          e);
      throw e;
    }
  }
}
