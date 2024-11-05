package io.games.poker_tournament_tracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.Season;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.SeasonDTO;
import io.games.poker_tournament_tracker.repos.GameRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.repos.SeasonRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import io.games.poker_tournament_tracker.util.ReferencedWarning;

import lombok.extern.slf4j.Slf4j;

/** Service class for managing Seasons. */
@Service
@Slf4j
public class SeasonService {

  private final SeasonRepository seasonRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;
  private final GameRepository gameRepository;

  @Autowired
  public SeasonService(
      SeasonRepository seasonRepository,
      SeasonPlayerRepository seasonPlayerRepository,
      GameRepository gameRepository) {
    this.seasonRepository = seasonRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.gameRepository = gameRepository;
  }

  /**
   * Retrieves all SeasonDTOs.
   *
   * @return a list of SeasonDTOs
   */
  public List<SeasonDTO> findAll() {
    try {
      log.info("Retrieving all seasons");
      final List<Season> seasons = seasonRepository.findAll(Sort.by("seasonId"));
      return seasons.stream().map(season -> mapToDTO(season, new SeasonDTO())).toList();
    } catch (Exception e) {
      log.error("Error finding all seasons", e);
      throw new RuntimeException("Failed to retrieve all seasons", e);
    }
  }

  /**
   * Retrieves the season ID by name.
   *
   * @param name the name of the season
   * @return the season ID
   */
  public int getSeasonIdByName(String name) {
    try {
      log.info("Retrieving season ID for name: {}", name);
      return seasonRepository.findSeasonIdByName(name);
    } catch (Exception e) {
      log.error("Error retrieving season ID for name: {}", name, e);
      throw new RuntimeException("Failed to retrieve season ID for name: " + name, e);
    }
  }

  /**
   * Retrieves a SeasonDTO by its ID.
   *
   * @param seasonId the ID of the season
   * @return the SeasonDTO
   */
  public SeasonDTO get(final Integer seasonId) {
    try {
      log.info("Retrieving season with id: {}", seasonId);
      return seasonRepository
          .findById(seasonId)
          .map(season -> mapToDTO(season, new SeasonDTO()))
          .orElseThrow(() -> new NotFoundException("Season not found with id: " + seasonId));
    } catch (Exception e) {
      log.error("Error getting season with id: {}", seasonId, e);
      throw new RuntimeException("Failed to retrieve season with id: " + seasonId, e);
    }
  }

  /**
   * Creates a new Season.
   *
   * @param seasonDTO the DTO of the season to create
   * @return the ID of the created season
   */
  @Transactional
  public Integer create(final SeasonDTO seasonDTO) {
    try {
      final Season season = new Season();
      mapToEntity(seasonDTO, season);
      return seasonRepository.save(season).getSeasonId();
    } catch (Exception e) {
      log.error("Error creating season", e);
      throw new RuntimeException("Failed to create season", e);
    }
  }

  /**
   * Updates an existing Season.
   *
   * @param seasonId the ID of the season to update
   * @param seasonDTO the DTO of the season to update
   */
  @Transactional
  public void update(final Integer seasonId, final SeasonDTO seasonDTO) {
    try {
      log.info("Updating season with id: {}", seasonId);
      final Season season =
          seasonRepository
              .findById(seasonId)
              .orElseThrow(() -> new NotFoundException("Season not found with id: " + seasonId));
      mapToEntity(seasonDTO, season);
      seasonRepository.save(season);
    } catch (Exception e) {
      log.error("Error updating season with id: {}", seasonId, e);
      throw new RuntimeException("Failed to update season with id: " + seasonId, e);
    }
  }

  /**
   * Deletes a Season by its ID.
   *
   * @param seasonId the ID of the season to delete
   */
  @Transactional
  public void delete(final Integer seasonId) {
    try {
      log.info("Deleting season with id: {}", seasonId);
      seasonRepository.deleteById(seasonId);
    } catch (Exception e) {
      log.error("Error deleting season with id: {}", seasonId, e);
      throw new RuntimeException("Failed to delete season with id: " + seasonId, e);
    }
  }

  /**
   * Maps a Season entity to a SeasonDTO.
   *
   * @param season the Season entity
   * @param seasonDTO the SeasonDTO
   * @return the mapped SeasonDTO
   */
  private SeasonDTO mapToDTO(final Season season, final SeasonDTO seasonDTO) {
    seasonDTO.setSeasonId(season.getSeasonId());
    seasonDTO.setName(season.getName());
    seasonDTO.setStartDate(season.getStartDate());
    seasonDTO.setEndDate(season.getEndDate());
    seasonDTO.setCreatedAt(season.getCreatedAt());
    return seasonDTO;
  }

  /**
   * Maps a SeasonDTO to a Season entity.
   *
   * @param seasonDTO the SeasonDTO
   * @param season the Season entity
   * @return the mapped Season entity
   */
  private Season mapToEntity(final SeasonDTO seasonDTO, final Season season) {
    season.setName(seasonDTO.getName());
    season.setStartDate(seasonDTO.getStartDate());
    season.setEndDate(seasonDTO.getEndDate());
    season.setCreatedAt(seasonDTO.getCreatedAt());
    return season;
  }

  /**
   * Creates a new Season with the given name.
   *
   * @param seasonName the name of the season
   */
  @Transactional
  public void createSeason(String seasonName) {
    try {
      log.info("Creating season with name: {}", seasonName);
      SeasonDTO seasonDTO = new SeasonDTO();
      seasonDTO.setName(seasonName);
      seasonDTO.setStartDate(LocalDate.now());
      create(seasonDTO);
    } catch (Exception e) {
      log.error("Error creating season with name: {}", seasonName, e);
      throw new RuntimeException("Failed to create season with name: " + seasonName, e);
    }
  }

  /**
   * Retrieves referenced warning for a season by its ID.
   *
   * @param seasonId the ID of the season
   * @return the ReferencedWarning
   */
  public ReferencedWarning getReferencedWarning(final Integer seasonId) {
    try {
      log.info("Retrieving referenced warning for season with id: {}", seasonId);
      final ReferencedWarning referencedWarning = new ReferencedWarning();
      final Season season =
          seasonRepository
              .findById(seasonId)
              .orElseThrow(() -> new NotFoundException("Season not found with id: " + seasonId));
      final SeasonPlayer seasonSeasonPlayer = seasonPlayerRepository.findFirstBySeason(season);
      if (seasonSeasonPlayer != null) {
        referencedWarning.setKey("season.seasonPlayer.season.referenced");
        referencedWarning.addParam(seasonSeasonPlayer.getSeasonPlayerId());
        return referencedWarning;
      }
      final Game seasonGame = gameRepository.findFirstBySeason(season);
      if (seasonGame != null) {
        referencedWarning.setKey("season.game.season.referenced");
        referencedWarning.addParam(seasonGame.getGameId());
        return referencedWarning;
      }
      return null;
    } catch (Exception e) {
      log.error("Error retrieving referenced warning for season with id: {}", seasonId, e);
      throw new RuntimeException(
          "Failed to retrieve referenced warning for season with id: " + seasonId, e);
    }
  }
}
