package io.games.poker_tournament_tracker.service;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.Season;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.SeasonDTO;
import io.games.poker_tournament_tracker.repos.GameRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.repos.SeasonRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import io.games.poker_tournament_tracker.util.ReferencedWarning;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing Seasons. */
@Service
public class SeasonService {

  private static final Logger logger = LoggerFactory.getLogger(SeasonService.class);

  @Autowired private SeasonRepository seasonRepository;
  @Autowired private SeasonPlayerRepository seasonPlayerRepository;
  @Autowired private GameRepository gameRepository;

  /**
   * Retrieves all SeasonDTOs.
   *
   * @return a list of SeasonDTOs
   */
  public List<SeasonDTO> findAll() {
    try {
      logger.info("Retrieving all seasons");
      final List<Season> seasons = seasonRepository.findAll(Sort.by("seasonId"));
      return seasons.stream().map(season -> mapToDTO(season, new SeasonDTO())).toList();
    } catch (Exception e) {
      logger.error("Error finding all seasons", e);
      throw e;
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
      logger.info("Retrieving season ID for name: {}", name);
      return seasonRepository.findSeasonIdByName(name);
    } catch (Exception e) {
      logger.error("Error retrieving season ID for name: {}", name, e);
      throw e;
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
      logger.info("Retrieving season with id: {}", seasonId);
      return seasonRepository
          .findById(seasonId)
          .map(season -> mapToDTO(season, new SeasonDTO()))
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      logger.warn("Season not found with id: {}", seasonId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error getting season with id: {}", seasonId, e);
      throw e;
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
      logger.info("Creating new season");
      final Season season = new Season();
      mapToEntity(seasonDTO, season);
      return seasonRepository.save(season).getSeasonId();
    } catch (Exception e) {
      logger.error("Error creating season", e);
      throw e;
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
      logger.info("Updating season with id: {}", seasonId);
      final Season season = seasonRepository.findById(seasonId).orElseThrow(NotFoundException::new);
      mapToEntity(seasonDTO, season);
      seasonRepository.save(season);
    } catch (NotFoundException e) {
      logger.warn("Season not found with id: {}", seasonId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error updating season with id: {}", seasonId, e);
      throw e;
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
      logger.info("Deleting season with id: {}", seasonId);
      seasonRepository.deleteById(seasonId);
    } catch (Exception e) {
      logger.error("Error deleting season with id: {}", seasonId, e);
      throw e;
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
      logger.info("Creating season with name: {}", seasonName);
      SeasonDTO seasonDTO = new SeasonDTO();
      seasonDTO.setName(seasonName);
      seasonDTO.setStartDate(LocalDate.now());
      create(seasonDTO);
    } catch (Exception e) {
      logger.error("Error creating season with name: {}", seasonName, e);
      throw e;
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
      logger.info("Retrieving referenced warning for season with id: {}", seasonId);
      final ReferencedWarning referencedWarning = new ReferencedWarning();
      final Season season = seasonRepository.findById(seasonId).orElseThrow(NotFoundException::new);
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
      logger.error("Error retrieving referenced warning for season with id: {}", seasonId, e);
      throw e;
    }
  }
}
