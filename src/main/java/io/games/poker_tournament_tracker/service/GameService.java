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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private final GameRepository gameRepository;
  private final SeasonRepository seasonRepository;
  private final GameBuyInRepository gameBuyInRepository;
  private final GameResultRepository gameResultRepository;
  private final PlayerParticipationRepository playerParticipationRepository;

  public GameService(
      final GameRepository gameRepository,
      final SeasonRepository seasonRepository,
      final GameBuyInRepository gameBuyInRepository,
      final GameResultRepository gameResultRepository,
      final PlayerParticipationRepository playerParticipationRepository) {
    this.gameRepository = gameRepository;
    this.seasonRepository = seasonRepository;
    this.gameBuyInRepository = gameBuyInRepository;
    this.gameResultRepository = gameResultRepository;
    this.playerParticipationRepository = playerParticipationRepository;
  }

  public List<GameDTO> findAll() {
    final List<Game> games = gameRepository.findAll(Sort.by("gameId"));
    return games.stream().map(game -> mapToDTO(game, new GameDTO())).toList();
  }

  public int getGameId(int gameNumber) {
    return gameRepository.findGameIdByGameNumber(gameNumber);
  }

  public GameDTO get(final Integer gameId) {
    return gameRepository
        .findById(gameId)
        .map(game -> mapToDTO(game, new GameDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public Integer create(final GameDTO gameDTO) {
    final Game game = new Game();
    mapToEntity(gameDTO, game);
    return gameRepository.save(game).getGameId();
  }

  public void update(final Integer gameId, final GameDTO gameDTO) {
    final Game game = gameRepository.findById(gameId).orElseThrow(NotFoundException::new);
    mapToEntity(gameDTO, game);
    gameRepository.save(game);
  }

  public void delete(final Integer gameId) {
    gameRepository.deleteById(gameId);
  }

  private GameDTO mapToDTO(final Game game, final GameDTO gameDTO) {
    gameDTO.setGameId(game.getGameId());
    gameDTO.setGameNumber(game.getGameNumber());
    gameDTO.setStartTime(game.getStartTime());
    gameDTO.setEndTime(game.getEndTime());
    gameDTO.setCreatedAt(game.getCreatedAt());
    gameDTO.setSeason(game.getSeason() == null ? null : game.getSeason().getSeasonId());
    return gameDTO;
  }

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

  public Integer getSeasonIdByGameNumber(int gameNumber) {
    Game game =
        gameRepository
            .findByGameNumber(gameNumber)
            .orElseThrow(() -> new NotFoundException("Game not found"));
    return game.getSeason().getSeasonId();
  }

  public ReferencedWarning getReferencedWarning(final Integer gameId) {
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
  }
}
