package io.games.poker_tournament_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.games.poker_tournament_tracker.domain.Game;
import io.games.poker_tournament_tracker.domain.GameBuyIn;
import io.games.poker_tournament_tracker.domain.GameResult;
import io.games.poker_tournament_tracker.domain.PlayerParticipation;
import io.games.poker_tournament_tracker.domain.Season;
import io.games.poker_tournament_tracker.model.GameDTO;
import io.games.poker_tournament_tracker.repos.GameBuyInRepository;
import io.games.poker_tournament_tracker.repos.GameRepository;
import io.games.poker_tournament_tracker.repos.GameResultRepository;
import io.games.poker_tournament_tracker.repos.PlayerParticipationRepository;
import io.games.poker_tournament_tracker.repos.SeasonRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import io.games.poker_tournament_tracker.util.ReferencedWarning;

import lombok.extern.slf4j.Slf4j;

/** Service class for managing Games. */
@Service
@Slf4j
public class GameService {

  private final GameRepository gameRepository;
  private final SeasonRepository seasonRepository;
  private final GameBuyInRepository gameBuyInRepository;
  private final GameResultRepository gameResultRepository;
  private final PlayerParticipationRepository playerParticipationRepository;
  private SeasonService seasonService;

  @Autowired
  public GameService(
      GameRepository gameRepository,
      SeasonRepository seasonRepository,
      GameBuyInRepository gameBuyInRepository,
      GameResultRepository gameResultRepository,
      PlayerParticipationRepository playerParticipationRepository) {
    this.gameRepository = gameRepository;
    this.seasonRepository = seasonRepository;
    this.gameBuyInRepository = gameBuyInRepository;
    this.gameResultRepository = gameResultRepository;
    this.playerParticipationRepository = playerParticipationRepository;
  }

  @Autowired
  public void setSeasonService(SeasonService seasonService) {
    this.seasonService = seasonService;
  }

  /**
   * Retrieves all GameDTOs.
   *
   * @return a list of GameDTOs
   */
  public List<GameDTO> findAll() {
    try {
      log.info("Retrieving all games");
      final List<Game> games = gameRepository.findAll(Sort.by("gameId"));
      return games.stream().map(game -> mapToDTO(game, new GameDTO())).toList();
    } catch (Exception e) {
      log.error("Error finding all games", e);
      throw new RuntimeException("Failed to retrieve all games", e);
    }
  }

  /**
   * Retrieves a GameDTO by its game number.
   *
   * @param gameNumber the number of the game
   * @return the game ID
   */
  public int getGameId(int gameNumber) {
    try {
      log.info("Retrieving game ID for game number: {}", gameNumber);
      return gameRepository.findGameIdByGameNumber(gameNumber);
    } catch (Exception e) {
      log.error("Error retrieving game ID for game number: {}", gameNumber, e);
      throw new RuntimeException("Failed to retrieve game ID for game number: " + gameNumber, e);
    }
  }

  /**
   * Retrieves a GameDTO by its ID.
   *
   * @param gameId the ID of the game
   * @return the GameDTO
   */
  public GameDTO get(final Integer gameId) {
    try {
      log.info("Retrieving game with id: {}", gameId);
      return gameRepository
          .findById(gameId)
          .map(game -> mapToDTO(game, new GameDTO()))
          .orElseThrow(() -> new NotFoundException("Game not found with id: " + gameId));
    } catch (Exception e) {
      log.error("Error getting game with id: {}", gameId, e);
      throw new RuntimeException("Failed to retrieve game with id: " + gameId, e);
    }
  }

  /**
   * Creates a new Game.
   *
   * @param gameDTO the DTO of the game to create
   * @return the ID of the created game
   */
  @Transactional
  public Integer create(final GameDTO gameDTO) {
    try {
      log.info("Creating new game");
      final Game game = new Game();
      mapToEntity(gameDTO, game);
      return gameRepository.save(game).getGameId();
    } catch (Exception e) {
      log.error("Error creating game", e);
      throw new RuntimeException("Failed to create game", e);
    }
  }

  /**
   * Updates an existing Game.
   *
   * @param gameId the ID of the game to update
   * @param gameDTO the DTO of the game to update
   */
  @Transactional
  public void update(final Integer gameId, final GameDTO gameDTO) {
    try {
      log.info("Updating game with id: {}", gameId);
      final Game game =
          gameRepository
              .findById(gameId)
              .orElseThrow(() -> new NotFoundException("Game not found with id: " + gameId));
      mapToEntity(gameDTO, game);
      gameRepository.save(game);
    } catch (Exception e) {
      log.error("Error updating game with id: {}", gameId, e);
      throw new RuntimeException("Failed to update game with id: " + gameId, e);
    }
  }

  /**
   * Deletes a Game by its ID.
   *
   * @param gameId the ID of the game to delete
   */
  @Transactional
  public void delete(final Integer gameId) {
    try {
      log.info("Deleting game with id: {}", gameId);
      gameRepository.deleteById(gameId);
    } catch (Exception e) {
      log.error("Error deleting game with id: {}", gameId, e);
      throw new RuntimeException("Failed to delete game with id: " + gameId, e);
    }
  }

  /**
   * Maps a Game entity to a GameDTO.
   *
   * @param game the Game entity
   * @param gameDTO the GameDTO
   * @return the mapped GameDTO
   */
  private GameDTO mapToDTO(final Game game, final GameDTO gameDTO) {
    gameDTO.setGameId(game.getGameId());
    gameDTO.setGameNumber(game.getGameNumber());
    gameDTO.setStartTime(game.getStartTime());
    gameDTO.setEndTime(game.getEndTime());
    gameDTO.setCreatedAt(game.getCreatedAt());
    gameDTO.setSeason(game.getSeason() == null ? null : game.getSeason().getSeasonId());
    return gameDTO;
  }

  /**
   * Maps a GameDTO to a Game entity.
   *
   * @param gameDTO the GameDTO
   * @param game the Game entity
   * @return the mapped Game entity
   */
  private Game mapToEntity(final GameDTO gameDTO, final Game game) {
    game.setGameNumber(gameDTO.getGameNumber());
    game.setStartTime(gameDTO.getStartTime());
    game.setEndTime(gameDTO.getEndTime());
    game.setCreatedAt(gameDTO.getCreatedAt());
    final Season season =
        gameDTO.getSeason() == null
            ? null
            : seasonRepository
                .findById(gameDTO.getSeason())
                .orElseThrow(
                    () ->
                        new NotFoundException("Season not found with id: " + gameDTO.getSeason()));
    game.setSeason(season);
    return game;
  }

  /**
   * Retrieves the season ID by game number.
   *
   * @param gameNumber the number of the game
   * @return the season ID
   */
  public Integer getSeasonIdByGameNumber(int gameNumber) {
    try {
      log.info("Retrieving season ID for game number: {}", gameNumber);
      Game game =
          gameRepository
              .findByGameNumber(gameNumber)
              .orElseThrow(
                  () -> new NotFoundException("Game not found with game number: " + gameNumber));
      return game.getSeason().getSeasonId();
    } catch (Exception e) {
      log.error("Error retrieving season ID for game number: {}", gameNumber, e);
      throw new RuntimeException("Failed to retrieve season ID for game number: " + gameNumber, e);
    }
  }

  /**
   * Creates a new game for a specific season.
   *
   * @param seasonName the name of the season
   * @param gameNumber the number of the game
   */
  @Transactional
  public void createGame(String seasonName, int gameNumber) {
    try {
      log.info("Creating game for season: {}, game number: {}", seasonName, gameNumber);
      GameDTO gamesDTO = new GameDTO();
      gamesDTO.setSeason(seasonService.getSeasonIdByName(seasonName));
      gamesDTO.setGameNumber(gameNumber);
      create(gamesDTO);
    } catch (Exception e) {
      log.error("Error creating game for season: {}, game number: {}", seasonName, gameNumber, e);
      throw new RuntimeException(
          "Failed to create game for season: " + seasonName + ", game number: " + gameNumber, e);
    }
  }

  /**
   * Retrieves referenced warning for a game by its ID.
   *
   * @param gameId the ID of the game
   * @return the ReferencedWarning
   */
  public ReferencedWarning getReferencedWarning(final Integer gameId) {
    try {
      log.info("Retrieving referenced warning for game with id: {}", gameId);
      final ReferencedWarning referencedWarning = new ReferencedWarning();
      final Game game =
          gameRepository
              .findById(gameId)
              .orElseThrow(() -> new NotFoundException("Game not found with id: " + gameId));
      final GameBuyIn gameGameBuyIn = gameBuyInRepository.findFirstByGame(game);
      if (gameGameBuyIn != null) {
        referencedWarning.setKey("game.gameBuyIn.game.referenced");
        referencedWarning.addParam(gameGameBuyIn.getGameBuyInId());
        return referencedWarning;
      }
      final GameResult gameGameResult = gameResultRepository.findFirstByGame(game);
      if (gameGameResult != null) {
        referencedWarning.setKey("game.gameResult.game.referenced");
        referencedWarning.addParam(gameGameResult.getGameResultId());
        return referencedWarning;
      }
      final PlayerParticipation gamePlayerParticipation =
          playerParticipationRepository.findFirstByGame(game);
      if (gamePlayerParticipation != null) {
        referencedWarning.setKey("game.playerParticipation.game.referenced");
        referencedWarning.addParam(gamePlayerParticipation.getParticipationId());
        return referencedWarning;
      }
      return null;
    } catch (Exception e) {
      log.error("Error retrieving referenced warning for game with id: {}", gameId, e);
      throw new RuntimeException(
          "Failed to retrieve referenced warning for game with id: " + gameId, e);
    }
  }
}
