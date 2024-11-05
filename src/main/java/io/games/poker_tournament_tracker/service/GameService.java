package io.games.poker_tournament_tracker.service;

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
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing Games. */
@Service
public class GameService {

  private static final Logger logger = LoggerFactory.getLogger(GameService.class);

  @Autowired private GameRepository gameRepository;
  @Autowired private SeasonRepository seasonRepository;
  @Autowired private GameBuyInRepository gameBuyInRepository;
  @Autowired private GameResultRepository gameResultRepository;
  @Autowired private PlayerParticipationRepository playerParticipationRepository;
  @Autowired private SeasonService seasonService;
  @Autowired private GameService gameService;

  /**
   * Retrieves all GameDTOs.
   *
   * @return a list of GameDTOs
   */
  public List<GameDTO> findAll() {
    try {
      logger.info("Retrieving all games");
      final List<Game> games = gameRepository.findAll(Sort.by("gameId"));
      return games.stream().map(game -> mapToDTO(game, new GameDTO())).toList();
    } catch (Exception e) {
      logger.error("Error finding all games", e);
      throw e;
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
      logger.info("Retrieving game ID for game number: {}", gameNumber);
      return gameRepository.findGameIdByGameNumber(gameNumber);
    } catch (Exception e) {
      logger.error("Error retrieving game ID for game number: {}", gameNumber, e);
      throw e;
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
      logger.info("Retrieving game with id: {}", gameId);
      return gameRepository
          .findById(gameId)
          .map(game -> mapToDTO(game, new GameDTO()))
          .orElseThrow(NotFoundException::new);
    } catch (NotFoundException e) {
      logger.warn("Game not found with id: {}", gameId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error getting game with id: {}", gameId, e);
      throw e;
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
      logger.info("Creating new game");
      final Game game = new Game();
      mapToEntity(gameDTO, game);
      return gameRepository.save(game).getGameId();
    } catch (Exception e) {
      logger.error("Error creating game", e);
      throw e;
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
      logger.info("Updating game with id: {}", gameId);
      final Game game = gameRepository.findById(gameId).orElseThrow(NotFoundException::new);
      mapToEntity(gameDTO, game);
      gameRepository.save(game);
    } catch (NotFoundException e) {
      logger.warn("Game not found with id: {}", gameId, e);
      throw e;
    } catch (Exception e) {
      logger.error("Error updating game with id: {}", gameId, e);
      throw e;
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
      logger.info("Deleting game with id: {}", gameId);
      gameRepository.deleteById(gameId);
    } catch (Exception e) {
      logger.error("Error deleting game with id: {}", gameId, e);
      throw e;
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
                .orElseThrow(() -> new NotFoundException("season not found"));
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
      logger.info("Retrieving season ID for game number: {}", gameNumber);
      Game game =
          gameRepository
              .findByGameNumber(gameNumber)
              .orElseThrow(() -> new NotFoundException("Game not found"));
      return game.getSeason().getSeasonId();
    } catch (Exception e) {
      logger.error("Error retrieving season ID for game number: {}", gameNumber, e);
      throw e;
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
      logger.info("Creating game for season: {}, game number: {}", seasonName, gameNumber);
      GameDTO gamesDTO = new GameDTO();
      gamesDTO.setSeason(seasonService.getSeasonIdByName(seasonName));
      gamesDTO.setGameNumber(gameNumber);
      gameService.create(gamesDTO);
    } catch (Exception e) {
      logger.error(
          "Error creating game for season: {}, game number: {}", seasonName, gameNumber, e);
      throw e;
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
      logger.info("Retrieving referenced warning for game with id: {}", gameId);
      final ReferencedWarning referencedWarning = new ReferencedWarning();
      final Game game = gameRepository.findById(gameId).orElseThrow(NotFoundException::new);
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
      logger.error("Error retrieving referenced warning for game with id: {}", gameId, e);
      throw e;
    }
  }
}
