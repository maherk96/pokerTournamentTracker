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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PlayerParticipationService {

  private final PlayerParticipationRepository playerParticipationRepository;
  private final GameRepository gameRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;

  public PlayerParticipationService(
      final PlayerParticipationRepository playerParticipationRepository,
      final GameRepository gameRepository,
      final SeasonPlayerRepository seasonPlayerRepository) {
    this.playerParticipationRepository = playerParticipationRepository;
    this.gameRepository = gameRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
  }

  public List<PlayerParticipationDTO> findAll() {
    final List<PlayerParticipation> playerParticipations =
        playerParticipationRepository.findAll(Sort.by("participationId"));
    return playerParticipations.stream()
        .map(playerParticipation -> mapToDTO(playerParticipation, new PlayerParticipationDTO()))
        .toList();
  }

  public PlayerParticipationDTO get(final Integer participationId) {
    return playerParticipationRepository
        .findById(participationId)
        .map(playerParticipation -> mapToDTO(playerParticipation, new PlayerParticipationDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public Integer create(final PlayerParticipationDTO playerParticipationDTO) {
    final PlayerParticipation playerParticipation = new PlayerParticipation();
    mapToEntity(playerParticipationDTO, playerParticipation);
    return playerParticipationRepository.save(playerParticipation).getParticipationId();
  }

  public void update(
      final Integer participationId, final PlayerParticipationDTO playerParticipationDTO) {
    final PlayerParticipation playerParticipation =
        playerParticipationRepository.findById(participationId).orElseThrow(NotFoundException::new);
    mapToEntity(playerParticipationDTO, playerParticipation);
    playerParticipationRepository.save(playerParticipation);
  }

  public void delete(final Integer participationId) {
    playerParticipationRepository.deleteById(participationId);
  }

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
                .orElseThrow(() -> new NotFoundException("game not found"));
    playerParticipation.setGame(game);
    final SeasonPlayer seasonPlayer =
        playerParticipationDTO.getSeasonPlayer() == null
            ? null
            : seasonPlayerRepository
                .findById(playerParticipationDTO.getSeasonPlayer())
                .orElseThrow(() -> new NotFoundException("seasonPlayer not found"));
    playerParticipation.setSeasonPlayer(seasonPlayer);
    return playerParticipation;
  }
}
