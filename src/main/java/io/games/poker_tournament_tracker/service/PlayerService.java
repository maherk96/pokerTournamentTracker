package io.games.poker_tournament_tracker.service;

import io.games.poker_tournament_tracker.domain.Player;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.PlayerDTO;
import io.games.poker_tournament_tracker.repos.PlayerRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import io.games.poker_tournament_tracker.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;

  public PlayerService(
      final PlayerRepository playerRepository,
      final SeasonPlayerRepository seasonPlayerRepository) {
    this.playerRepository = playerRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
  }

  public List<PlayerDTO> findAll() {
    final List<Player> players = playerRepository.findAll(Sort.by("playerId"));
    return players.stream().map(player -> mapToDTO(player, new PlayerDTO())).toList();
  }

  public int getPlayerIdByName(String name) {
    return playerRepository.findPlayerIdByName(name);
  }

  public Integer getOrCreatePlayerIdByName(String name) {
    List<Player> players = playerRepository.findAll();
    return players.stream()
        .filter(player -> player.getName().equals(name))
        .findFirst()
        .map(Player::getPlayerId)
        .orElseGet(
            () -> {
              PlayerDTO newPlayer = new PlayerDTO();
              newPlayer.setName(name);
              Integer playerId = create(newPlayer);
              System.out.println("Created new player with ID: " + playerId);
              return playerId;
            });
  }

  public PlayerDTO get(final Integer playerId) {
    return playerRepository
        .findById(playerId)
        .map(player -> mapToDTO(player, new PlayerDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public Integer create(final PlayerDTO playerDTO) {
    final Player player = new Player();
    mapToEntity(playerDTO, player);
    return playerRepository.save(player).getPlayerId();
  }

  public void update(final Integer playerId, final PlayerDTO playerDTO) {
    final Player player = playerRepository.findById(playerId).orElseThrow(NotFoundException::new);
    mapToEntity(playerDTO, player);
    playerRepository.save(player);
  }

  public void delete(final Integer playerId) {
    playerRepository.deleteById(playerId);
  }

  private PlayerDTO mapToDTO(final Player player, final PlayerDTO playerDTO) {
    playerDTO.setPlayerId(player.getPlayerId());
    playerDTO.setName(player.getName());
    playerDTO.setCreatedAt(player.getCreatedAt());
    return playerDTO;
  }

  private Player mapToEntity(final PlayerDTO playerDTO, final Player player) {
    player.setName(playerDTO.getName());
    player.setCreatedAt(playerDTO.getCreatedAt());
    return player;
  }

  public ReferencedWarning getReferencedWarning(final Integer playerId) {
    final ReferencedWarning referencedWarning = new ReferencedWarning();
    final Player player = playerRepository.findById(playerId).orElseThrow(NotFoundException::new);
    final SeasonPlayer playerSeasonPlayer = seasonPlayerRepository.findFirstByPlayer(player);
    if (playerSeasonPlayer != null) {
      referencedWarning.setKey("player.seasonPlayer.player.referenced");
      referencedWarning.addParam(playerSeasonPlayer.getSeasonPlayerId());
      return referencedWarning;
    }
    return null;
  }
}
