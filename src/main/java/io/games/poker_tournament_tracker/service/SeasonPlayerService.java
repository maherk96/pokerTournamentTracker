package io.games.poker_tournament_tracker.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.games.poker_tournament_tracker.domain.GameBuyIn;
import io.games.poker_tournament_tracker.domain.GameResult;
import io.games.poker_tournament_tracker.domain.Player;
import io.games.poker_tournament_tracker.domain.PlayerParticipation;
import io.games.poker_tournament_tracker.domain.Season;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.SeasonPlayerDTO;
import io.games.poker_tournament_tracker.repos.GameBuyInRepository;
import io.games.poker_tournament_tracker.repos.GameResultRepository;
import io.games.poker_tournament_tracker.repos.PlayerParticipationRepository;
import io.games.poker_tournament_tracker.repos.PlayerRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.repos.SeasonRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import io.games.poker_tournament_tracker.util.ReferencedWarning;

import lombok.extern.slf4j.Slf4j;

/** Service class for managing Season Players. */
@Service
@Slf4j
public class SeasonPlayerService {

  private final SeasonPlayerRepository seasonPlayerRepository;
  private final SeasonRepository seasonRepository;
  private final PlayerRepository playerRepository;
  private final GameBuyInRepository gameBuyInRepository;
  private final GameResultRepository gameResultRepository;
  private final PlayerParticipationRepository playerParticipationRepository;
  private final SeasonService seasonService;
  private final PlayerService playerService;
  private final GameService gameService;

  @Autowired
  public SeasonPlayerService(
      SeasonPlayerRepository seasonPlayerRepository,
      SeasonRepository seasonRepository,
      PlayerRepository playerRepository,
      GameBuyInRepository gameBuyInRepository,
      GameResultRepository gameResultRepository,
      PlayerParticipationRepository playerParticipationRepository,
      SeasonService seasonService,
      PlayerService playerService,
      GameService gameService) {
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.seasonRepository = seasonRepository;
    this.playerRepository = playerRepository;
    this.gameBuyInRepository = gameBuyInRepository;
    this.gameResultRepository = gameResultRepository;
    this.playerParticipationRepository = playerParticipationRepository;
    this.seasonService = seasonService;
    this.playerService = playerService;
    this.gameService = gameService;
  }

  /**
   * Retrieves all SeasonPlayerDTOs.
   *
   * @return a list of SeasonPlayerDTOs
   */
  public List<SeasonPlayerDTO> findAll() {
    try {
      log.info("Retrieving all season players");
      final List<SeasonPlayer> seasonPlayers =
          seasonPlayerRepository.findAll(Sort.by("seasonPlayerId"));
      return seasonPlayers.stream()
          .map(seasonPlayer -> mapToDTO(seasonPlayer, new SeasonPlayerDTO()))
          .toList();
    } catch (Exception e) {
      log.error("Error finding all season players", e);
      throw new RuntimeException("Failed to retrieve all season players", e);
    }
  }

  /**
   * Retrieves a SeasonPlayerDTO by its ID.
   *
   * @param seasonPlayerId the ID of the SeasonPlayer
   * @return the SeasonPlayerDTO
   */
  public SeasonPlayerDTO get(final Integer seasonPlayerId) {
    try {
      log.info("Retrieving season player with id: {}", seasonPlayerId);
      return seasonPlayerRepository
          .findById(seasonPlayerId)
          .map(seasonPlayer -> mapToDTO(seasonPlayer, new SeasonPlayerDTO()))
          .orElseThrow(
              () -> new NotFoundException("Season player not found with id: " + seasonPlayerId));
    } catch (Exception e) {
      log.error("Error getting season player with id: {}", seasonPlayerId, e);
      throw new RuntimeException("Failed to retrieve season player with id: " + seasonPlayerId, e);
    }
  }

  /**
   * Creates a new SeasonPlayer.
   *
   * @param seasonPlayerDTO the DTO of the SeasonPlayer to create
   * @return the ID of the created SeasonPlayer
   */
  @Transactional
  public Integer create(final SeasonPlayerDTO seasonPlayerDTO) {
    try {
      log.info("Creating new season player");
      final SeasonPlayer seasonPlayer = new SeasonPlayer();
      mapToEntity(seasonPlayerDTO, seasonPlayer);
      return seasonPlayerRepository.save(seasonPlayer).getSeasonPlayerId();
    } catch (Exception e) {
      log.error("Error creating season player", e);
      throw new RuntimeException("Failed to create season player", e);
    }
  }

  /**
   * Updates an existing SeasonPlayer.
   *
   * @param seasonPlayerId the ID of the SeasonPlayer to update
   * @param seasonPlayerDTO the DTO of the SeasonPlayer to update
   */
  @Transactional
  public void update(final Integer seasonPlayerId, final SeasonPlayerDTO seasonPlayerDTO) {
    try {
      log.info("Updating season player with id: {}", seasonPlayerId);
      final SeasonPlayer seasonPlayer =
          seasonPlayerRepository
              .findById(seasonPlayerId)
              .orElseThrow(
                  () ->
                      new NotFoundException("Season player not found with id: " + seasonPlayerId));
      mapToEntity(seasonPlayerDTO, seasonPlayer);
      seasonPlayerRepository.save(seasonPlayer);
    } catch (Exception e) {
      log.error("Error updating season player with id: {}", seasonPlayerId, e);
      throw new RuntimeException("Failed to update season player with id: " + seasonPlayerId, e);
    }
  }

  /**
   * Deletes a SeasonPlayer by its ID.
   *
   * @param seasonPlayerId the ID of the SeasonPlayer to delete
   */
  @Transactional
  public void delete(final Integer seasonPlayerId) {
    try {
      log.info("Deleting season player with id: {}", seasonPlayerId);
      seasonPlayerRepository.deleteById(seasonPlayerId);
    } catch (Exception e) {
      log.error("Error deleting season player with id: {}", seasonPlayerId, e);
      throw new RuntimeException("Failed to delete season player with id: " + seasonPlayerId, e);
    }
  }

  /**
   * Maps a SeasonPlayer entity to a SeasonPlayerDTO.
   *
   * @param seasonPlayer the SeasonPlayer entity
   * @param seasonPlayerDTO the SeasonPlayerDTO
   * @return the mapped SeasonPlayerDTO
   */
  private SeasonPlayerDTO mapToDTO(
      final SeasonPlayer seasonPlayer, final SeasonPlayerDTO seasonPlayerDTO) {
    seasonPlayerDTO.setSeasonPlayerId(seasonPlayer.getSeasonPlayerId());
    seasonPlayerDTO.setAllocatedPotSize(seasonPlayer.getAllocatedPotSize());
    seasonPlayerDTO.setMinBuyIn(seasonPlayer.getMinBuyIn());
    seasonPlayerDTO.setCurrentPotSize(seasonPlayer.getCurrentPotSize());
    seasonPlayerDTO.setSeason(
        seasonPlayer.getSeason() == null ? null : seasonPlayer.getSeason().getSeasonId());
    seasonPlayerDTO.setPlayer(
        seasonPlayer.getPlayer() == null ? null : seasonPlayer.getPlayer().getPlayerId());
    return seasonPlayerDTO;
  }

  /**
   * Maps a SeasonPlayerDTO to a SeasonPlayer entity.
   *
   * @param seasonPlayerDTO the SeasonPlayerDTO
   * @param seasonPlayer the SeasonPlayer entity
   * @return the mapped SeasonPlayer entity
   */
  private SeasonPlayer mapToEntity(
      final SeasonPlayerDTO seasonPlayerDTO, final SeasonPlayer seasonPlayer) {
    seasonPlayer.setAllocatedPotSize(seasonPlayerDTO.getAllocatedPotSize());
    seasonPlayer.setMinBuyIn(seasonPlayerDTO.getMinBuyIn());
    seasonPlayer.setCurrentPotSize(seasonPlayerDTO.getCurrentPotSize());
    final Season season =
        seasonPlayerDTO.getSeason() == null
            ? null
            : seasonRepository
                .findById(seasonPlayerDTO.getSeason())
                .orElseThrow(() -> new NotFoundException("Season not found"));
    seasonPlayer.setSeason(season);
    final Player player =
        seasonPlayerDTO.getPlayer() == null
            ? null
            : playerRepository
                .findById(seasonPlayerDTO.getPlayer())
                .orElseThrow(() -> new NotFoundException("Player not found"));
    seasonPlayer.setPlayer(player);
    return seasonPlayer;
  }

  /**
   * Retrieves the SeasonPlayer ID by player name and season ID.
   *
   * @param playerName the name of the player
   * @param seasonId the ID of the season
   * @return the SeasonPlayer ID
   */
  public Integer getSeasonPlayerIdByPlayerNameAndSeasonId(String playerName, Integer seasonId) {
    try {
      log.info(
          "Retrieving season player ID for player name: {} and season ID: {}",
          playerName,
          seasonId);
      Player player =
          playerRepository
              .findByName(playerName)
              .orElseThrow(() -> new NotFoundException("Player not found"));
      Season season =
          seasonRepository
              .findById(seasonId)
              .orElseThrow(() -> new NotFoundException("Season not found"));
      SeasonPlayer seasonPlayer =
          seasonPlayerRepository
              .findByPlayerAndSeason(player, season)
              .orElseThrow(() -> new NotFoundException("Season player not found"));
      return seasonPlayer.getSeasonPlayerId();
    } catch (Exception e) {
      log.error(
          "Error retrieving season player ID for player name: {} and season ID: {}",
          playerName,
          seasonId,
          e);
      throw new RuntimeException(
          "Failed to retrieve season player ID for player name: "
              + playerName
              + " and season ID: "
              + seasonId,
          e);
    }
  }

  /**
   * Creates a new SeasonPlayer for a specific season and player.
   *
   * @param seasonName the name of the season
   * @param playerName the name of the player
   * @param minBuyIn the minimum buy-in amount
   * @param allocatedPotSize the allocated pot size
   */
  @Transactional
  public void createSeasonPlayers(
      String seasonName, String playerName, Double minBuyIn, Double allocatedPotSize) {
    try {
      log.info("Creating season player for season: {}, player: {}", seasonName, playerName);
      var seasonPlayerDTO = new SeasonPlayerDTO();
      seasonPlayerDTO.setSeason(seasonService.getSeasonIdByName(seasonName));
      var playerId = playerService.getOrCreatePlayerIdByName(playerName);
      seasonPlayerDTO.setPlayer(playerId);
      seasonPlayerDTO.setMinBuyIn(BigDecimal.valueOf(minBuyIn));
      seasonPlayerDTO.setAllocatedPotSize(BigDecimal.valueOf(allocatedPotSize));
      seasonPlayerDTO.setCurrentPotSize(BigDecimal.valueOf(allocatedPotSize));
      create(seasonPlayerDTO);
    } catch (Exception e) {
      log.error(
          "Error creating season player for season: {}, player: {}", seasonName, playerName, e);
      throw new RuntimeException(
          "Failed to create season player for season: " + seasonName + ", player: " + playerName,
          e);
    }
  }

  /**
   * Retrieves referenced warning for a season player by its ID.
   *
   * @param seasonPlayerId the ID of the season player
   * @return the ReferencedWarning
   */
  public ReferencedWarning getReferencedWarning(final Integer seasonPlayerId) {
    try {
      log.info("Retrieving referenced warning for season player with id: {}", seasonPlayerId);
      final ReferencedWarning referencedWarning = new ReferencedWarning();
      final SeasonPlayer seasonPlayer =
          seasonPlayerRepository
              .findById(seasonPlayerId)
              .orElseThrow(
                  () ->
                      new NotFoundException("Season player not found with id: " + seasonPlayerId));
      final GameBuyIn seasonPlayerGameBuyIn =
          gameBuyInRepository.findFirstBySeasonPlayer(seasonPlayer);
      if (seasonPlayerGameBuyIn != null) {
        referencedWarning.setKey("seasonPlayer.gameBuyIn.seasonPlayer.referenced");
        referencedWarning.addParam(seasonPlayerGameBuyIn.getGameBuyInId());
        return referencedWarning;
      }
      final GameResult seasonPlayerGameResult =
          gameResultRepository.findFirstBySeasonPlayer(seasonPlayer);
      if (seasonPlayerGameResult != null) {
        referencedWarning.setKey("seasonPlayer.gameResult.seasonPlayer.referenced");
        referencedWarning.addParam(seasonPlayerGameResult.getGameResultId());
        return referencedWarning;
      }
      final PlayerParticipation seasonPlayerPlayerParticipation =
          playerParticipationRepository.findFirstBySeasonPlayer(seasonPlayer);
      if (seasonPlayerPlayerParticipation != null) {
        referencedWarning.setKey("seasonPlayer.playerParticipation.seasonPlayer.referenced");
        referencedWarning.addParam(seasonPlayerPlayerParticipation.getParticipationId());
        return referencedWarning;
      }
      return null;
    } catch (Exception e) {
      log.error(
          "Error retrieving referenced warning for season player with id: {}", seasonPlayerId, e);
      throw new RuntimeException(
          "Failed to retrieve referenced warning for season player with id: " + seasonPlayerId, e);
    }
  }
}
