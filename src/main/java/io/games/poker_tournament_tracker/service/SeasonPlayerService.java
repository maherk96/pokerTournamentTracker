package io.games.poker_tournament_tracker.service;

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
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SeasonPlayerService {

  private final SeasonPlayerRepository seasonPlayerRepository;
  private final SeasonRepository seasonRepository;
  private final PlayerRepository playerRepository;
  private final GameBuyInRepository gameBuyInRepository;
  private final GameResultRepository gameResultRepository;
  private final PlayerParticipationRepository playerParticipationRepository;

  public SeasonPlayerService(
      final SeasonPlayerRepository seasonPlayerRepository,
      final SeasonRepository seasonRepository,
      final PlayerRepository playerRepository,
      final GameBuyInRepository gameBuyInRepository,
      final GameResultRepository gameResultRepository,
      final PlayerParticipationRepository playerParticipationRepository) {
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.seasonRepository = seasonRepository;
    this.playerRepository = playerRepository;
    this.gameBuyInRepository = gameBuyInRepository;
    this.gameResultRepository = gameResultRepository;
    this.playerParticipationRepository = playerParticipationRepository;
  }

  public List<SeasonPlayerDTO> findAll() {
    final List<SeasonPlayer> seasonPlayers =
        seasonPlayerRepository.findAll(Sort.by("seasonPlayerId"));
    return seasonPlayers.stream()
        .map(seasonPlayer -> mapToDTO(seasonPlayer, new SeasonPlayerDTO()))
        .toList();
  }

  public SeasonPlayerDTO get(final Integer seasonPlayerId) {
    return seasonPlayerRepository
        .findById(seasonPlayerId)
        .map(seasonPlayer -> mapToDTO(seasonPlayer, new SeasonPlayerDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public Integer create(final SeasonPlayerDTO seasonPlayerDTO) {
    final SeasonPlayer seasonPlayer = new SeasonPlayer();
    mapToEntity(seasonPlayerDTO, seasonPlayer);
    return seasonPlayerRepository.save(seasonPlayer).getSeasonPlayerId();
  }

  public void update(final Integer seasonPlayerId, final SeasonPlayerDTO seasonPlayerDTO) {
    final SeasonPlayer seasonPlayer =
        seasonPlayerRepository.findById(seasonPlayerId).orElseThrow(NotFoundException::new);
    mapToEntity(seasonPlayerDTO, seasonPlayer);
    seasonPlayerRepository.save(seasonPlayer);
  }

  public void delete(final Integer seasonPlayerId) {
    seasonPlayerRepository.deleteById(seasonPlayerId);
  }

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
                .orElseThrow(() -> new NotFoundException("season not found"));
    seasonPlayer.setSeason(season);
    final Player player =
        seasonPlayerDTO.getPlayer() == null
            ? null
            : playerRepository
                .findById(seasonPlayerDTO.getPlayer())
                .orElseThrow(() -> new NotFoundException("player not found"));
    seasonPlayer.setPlayer(player);
    return seasonPlayer;
  }

  public Integer getSeasonPlayerIdByPlayerNameAndSeasonId(String playerName, Integer seasonId) {
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
  }

  public ReferencedWarning getReferencedWarning(final Integer seasonPlayerId) {
    final ReferencedWarning referencedWarning = new ReferencedWarning();
    final SeasonPlayer seasonPlayer =
        seasonPlayerRepository.findById(seasonPlayerId).orElseThrow(NotFoundException::new);
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
  }
}
