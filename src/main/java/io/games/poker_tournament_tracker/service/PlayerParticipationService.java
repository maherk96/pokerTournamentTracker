package io.games.poker_tournament_tracker.service;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.PlayerParticipation;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.PlayerParticipationDTO;
import io.games.poker_tournament_tracker.repos.GameRepository;
import io.games.poker_tournament_tracker.repos.PlayerParticipationRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing Player Participations. */
@Service
@Slf4j
public class PlayerParticipationService {

  private final PlayerParticipationRepository playerParticipationRepository;
  private final GameRepository gameRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;
  private final SeasonPlayerService seasonPlayerService;
  private final GameService gameService;

  @Autowired
  public PlayerParticipationService(
      PlayerParticipationRepository playerParticipationRepository,
      GameRepository gameRepository,
      SeasonPlayerRepository seasonPlayerRepository,
      SeasonPlayerService seasonPlayerService,
      GameService gameService) {
    this.playerParticipationRepository = playerParticipationRepository;
    this.gameRepository = gameRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.seasonPlayerService = seasonPlayerService;
    this.gameService = gameService;
  }

  /**
   * Retrieves all PlayerParticipationDTOs.
   *
   * @return a list of PlayerParticipationDTOs
   */
  public List<PlayerParticipationDTO> findAll() {
    try {
      log.info("Retrieving all player participations");
      final List<PlayerParticipation> playerParticipations =
          playerParticipationRepository.findAll(Sort.by("participationId"));
      return playerParticipations.stream()
          .map(playerParticipation -> mapToDTO(playerParticipation, new PlayerParticipationDTO()))
          .toList();
    } catch (Exception e) {
      log.error("Error finding all player participations", e);
      throw new RuntimeException("Failed to retrieve all player participations", e);
    }
  }

  /**
   * Retrieves a PlayerParticipationDTO by its ID.
   *
   * @param participationId the ID of the PlayerParticipation
   * @return the PlayerParticipationDTO
   */
  public PlayerParticipationDTO get(final Integer participationId) {
    try {
      log.info("Retrieving player participation with id: {}", participationId);
      return playerParticipationRepository
          .findById(participationId)
          .map(playerParticipation -> mapToDTO(playerParticipation, new PlayerParticipationDTO()))
          .orElseThrow(
              () ->
                  new NotFoundException(
                      "Player participation not found with id: " + participationId));
    } catch (Exception e) {
      log.error("Error getting player participation with id: {}", participationId, e);
      throw new RuntimeException(
          "Failed to retrieve player participation with id: " + participationId, e);
    }
  }

  /**
   * Creates a new PlayerParticipation.
   *
   * @param playerParticipationDTO the DTO of the PlayerParticipation to create
   * @return the ID of the created PlayerParticipation
   */
  @Transactional
  public Integer create(final PlayerParticipationDTO playerParticipationDTO) {
    try {
      log.info("Creating new player participation");
      final PlayerParticipation playerParticipation = new PlayerParticipation();
      mapToEntity(playerParticipationDTO, playerParticipation);
      return playerParticipationRepository.save(playerParticipation).getParticipationId();
    } catch (Exception e) {
      log.error("Error creating player participation", e);
      throw new RuntimeException("Failed to create player participation", e);
    }
  }

  /**
   * Updates an existing PlayerParticipation.
   *
   * @param participationId the ID of the PlayerParticipation to update
   * @param playerParticipationDTO the DTO of the PlayerParticipation to update
   */
  @Transactional
  public void update(
      final Integer participationId, final PlayerParticipationDTO playerParticipationDTO) {
    try {
      log.info("Updating player participation with id: {}", participationId);
      final PlayerParticipation playerParticipation =
          playerParticipationRepository
              .findById(participationId)
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Player participation not found with id: " + participationId));
      mapToEntity(playerParticipationDTO, playerParticipation);
      playerParticipationRepository.save(playerParticipation);
    } catch (Exception e) {
      log.error("Error updating player participation with id: {}", participationId, e);
      throw new RuntimeException(
          "Failed to update player participation with id: " + participationId, e);
    }
  }

  /**
   * Deletes a PlayerParticipation by its ID.
   *
   * @param participationId the ID of the PlayerParticipation to delete
   */
  @Transactional
  public void delete(final Integer participationId) {
    try {
      log.info("Deleting player participation with id: {}", participationId);
      playerParticipationRepository.deleteById(participationId);
    } catch (Exception e) {
      log.error("Error deleting player participation with id: {}", participationId, e);
      throw new RuntimeException(
          "Failed to delete player participation with id: " + participationId, e);
    }
  }

  /**
   * Maps a PlayerParticipation entity to a PlayerParticipationDTO.
   *
   * @param playerParticipation the PlayerParticipation entity
   * @param playerParticipationDTO the PlayerParticipationDTO
   * @return the mapped PlayerParticipationDTO
   */
  private PlayerParticipationDTO mapToDTO(
      final PlayerParticipation playerParticipation,
      final PlayerParticipationDTO playerParticipationDTO) {
    playerParticipationDTO.setParticipationId(playerParticipation.getParticipationId());
    playerParticipationDTO.setParticipated(playerParticipation.getParticipated());
    playerParticipationDTO.setParticipationTime(playerParticipation.getParticipationTime());
    playerParticipationDTO.setGame(
        playerParticipation.getGame() == null ? null : playerParticipation.getGame().getGameId());
    playerParticipationDTO.setSeasonPlayer(
        playerParticipation.getSeasonPlayer() == null
            ? null
            : playerParticipation.getSeasonPlayer().getSeasonPlayerId());
    return playerParticipationDTO;
  }

  /**
   * Maps a PlayerParticipationDTO to a PlayerParticipation entity.
   *
   * @param playerParticipationDTO the PlayerParticipationDTO
   * @param playerParticipation the PlayerParticipation entity
   * @return the mapped PlayerParticipation entity
   */
  private PlayerParticipation mapToEntity(
      final PlayerParticipationDTO playerParticipationDTO,
      final PlayerParticipation playerParticipation) {
    playerParticipation.setParticipated(playerParticipationDTO.getParticipated());
    playerParticipation.setParticipationTime(playerParticipationDTO.getParticipationTime());
    final Game game =
        playerParticipationDTO.getGame() == null
            ? null
            : gameRepository
                .findById(playerParticipationDTO.getGame())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "Game not found with id: " + playerParticipationDTO.getGame()));
    playerParticipation.setGame(game);
    final SeasonPlayer seasonPlayer =
        playerParticipationDTO.getSeasonPlayer() == null
            ? null
            : seasonPlayerRepository
                .findById(playerParticipationDTO.getSeasonPlayer())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            "Season player not found with id: "
                                + playerParticipationDTO.getSeasonPlayer()));
    playerParticipation.setSeasonPlayer(seasonPlayer);
    return playerParticipation;
  }

  /**
   * Creates a new PlayerParticipation for a specific game and player.
   *
   * @param playerName the name of the player
   * @param playerParticipation the participation status
   * @param gameNumber the number of the game
   */
  @Transactional
  public void createPlayerParticipation(
      String playerName,
      io.games.poker_tournament_tracker.service.impl.PlayerParticipation playerParticipation,
      int gameNumber) {
    try {
      log.info(
          "Creating player participation for player: {}, game number: {}", playerName, gameNumber);
      PlayerParticipationDTO playerParticipationDTO = new PlayerParticipationDTO();
      playerParticipationDTO.setSeasonPlayer(
          seasonPlayerService.getSeasonPlayerIdByPlayerNameAndSeasonId(
              playerName, gameService.getSeasonIdByGameNumber(gameNumber)));
      playerParticipationDTO.setGame(gameService.getGameId(gameNumber));
      playerParticipationDTO.setParticipated(
          playerParticipation.equals(
              io.games.poker_tournament_tracker.service.impl.PlayerParticipation.YES));
      create(playerParticipationDTO);
    } catch (Exception e) {
      log.error(
          "Error creating player participation for player: {}, game number: {}",
          playerName,
          gameNumber,
          e);
      throw new RuntimeException(
          "Failed to create player participation for player: "
              + playerName
              + ", game number: "
              + gameNumber,
          e);
    }
  }
}
