package io.games.poker_tournament_tracker.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.games.poker_tournament_tracker.domain.GameBuyIn;
import io.games.poker_tournament_tracker.model.GameBuyInDTO;
import io.games.poker_tournament_tracker.repos.GameBuyInRepository;
import io.games.poker_tournament_tracker.repos.GameRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;

import lombok.extern.slf4j.Slf4j;

/** Service class for managing Game Buy-Ins. */
@Service
@Slf4j
public class GameBuyInService {

  private final GameBuyInRepository gameBuyInRepository;
  private final GameRepository gameRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;
  private final GameService gameService;
  private final SeasonPlayerService seasonPlayerService;

  @Autowired
  public GameBuyInService(
      GameBuyInRepository gameBuyInRepository,
      GameRepository gameRepository,
      SeasonPlayerRepository seasonPlayerRepository,
      GameService gameService,
      SeasonPlayerService seasonPlayerService) {
    this.gameBuyInRepository = gameBuyInRepository;
    this.gameRepository = gameRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.gameService = gameService;
    this.seasonPlayerService = seasonPlayerService;
  }

  /**
   * Retrieves all GameBuyInDTOs.
   *
   * @return a list of GameBuyInDTOs
   */
  public List<GameBuyInDTO> findAll() {
    log.info("Retrieving all game buy-ins");
    List<GameBuyIn> gameBuyIns = gameBuyInRepository.findAll(Sort.by("gameBuyInId"));
    return gameBuyIns.stream().map(this::mapToDTO).toList();
  }

  /**
   * Retrieves a GameBuyInDTO by its ID.
   *
   * @param gameBuyInId the ID of the game buy-in
   * @return the GameBuyInDTO
   */
  public GameBuyInDTO get(Integer gameBuyInId) {
    log.info("Retrieving game buy-in with id: {}", gameBuyInId);
    return gameBuyInRepository
        .findById(gameBuyInId)
        .map(this::mapToDTO)
        .orElseThrow(() -> new NotFoundException("Game buy-in not found"));
  }

  /**
   * Creates a new GameBuyIn.
   *
   * @param gameBuyInDTO the DTO of the game buy-in to create
   * @return the ID of the created game buy-in
   */
  @Transactional
  public Integer create(GameBuyInDTO gameBuyInDTO) {
    log.info("Creating new game buy-in");
    GameBuyIn gameBuyIn = new GameBuyIn();
    mapToEntity(gameBuyInDTO, gameBuyIn);
    return gameBuyInRepository.save(gameBuyIn).getGameBuyInId();
  }

  /**
   * Updates an existing GameBuyIn.
   *
   * @param gameBuyInId the ID of the game buy-in to update
   * @param gameBuyInDTO the DTO of the game buy-in to update
   */
  @Transactional
  public void update(Integer gameBuyInId, GameBuyInDTO gameBuyInDTO) {
    log.info("Updating game buy-in with id: {}", gameBuyInId);
    GameBuyIn gameBuyIn =
        gameBuyInRepository
            .findById(gameBuyInId)
            .orElseThrow(() -> new NotFoundException("Game buy-in not found"));
    mapToEntity(gameBuyInDTO, gameBuyIn);
    gameBuyInRepository.save(gameBuyIn);
  }

  /**
   * Deletes a GameBuyIn by its ID.
   *
   * @param gameBuyInId the ID of the game buy-in to delete
   */
  @Transactional
  public void delete(Integer gameBuyInId) {
    log.info("Deleting game buy-in with id: {}", gameBuyInId);
    gameBuyInRepository.deleteById(gameBuyInId);
  }

  /**
   * Maps a GameBuyIn entity to a GameBuyInDTO.
   *
   * @param gameBuyIn the GameBuyIn entity
   * @return the mapped GameBuyInDTO
   */
  private GameBuyInDTO mapToDTO(GameBuyIn gameBuyIn) {
    GameBuyInDTO gameBuyInDTO = new GameBuyInDTO();
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
   */
  private void mapToEntity(GameBuyInDTO gameBuyInDTO, GameBuyIn gameBuyIn) {
    gameBuyIn.setBuyInAmount(gameBuyInDTO.getBuyInAmount());
    gameBuyIn.setGame(
        gameBuyInDTO.getGame() == null
            ? null
            : gameRepository
                .findById(gameBuyInDTO.getGame())
                .orElseThrow(() -> new NotFoundException("Game not found")));
    gameBuyIn.setSeasonPlayer(
        gameBuyInDTO.getSeasonPlayer() == null
            ? null
            : seasonPlayerRepository
                .findById(gameBuyInDTO.getSeasonPlayer())
                .orElseThrow(() -> new NotFoundException("Season player not found")));
  }

  /**
   * Creates a new GameBuyIn with the given game number, player name, and buy-in amount.
   *
   * @param gameNumber the number of the game
   * @param playerName the name of the player
   * @param buyInAmount the amount of the buy-in
   */
  @Transactional
  public void createGameBuyIn(int gameNumber, String playerName, double buyInAmount) {
    try {
      log.info("Creating game buy-in for game number: {}, player name: {}", gameNumber, playerName);
      GameBuyInDTO gameBuyInDTO = new GameBuyInDTO();
      gameBuyInDTO.setGame(gameService.getGameId(gameNumber));
      Integer seasonIdByGameNumber = gameService.getSeasonIdByGameNumber(gameNumber);
      gameBuyInDTO.setSeasonPlayer(
          seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
              playerName, seasonIdByGameNumber));
      gameBuyInDTO.setBuyInAmount(BigDecimal.valueOf(buyInAmount));
      create(gameBuyInDTO);
    } catch (Exception e) {
      log.error(
          "Error creating game buy-in for game number: {}, player name: {}",
          gameNumber,
          playerName,
          e);
      throw new RuntimeException("Error creating game buy-in", e);
    }
  }
}
