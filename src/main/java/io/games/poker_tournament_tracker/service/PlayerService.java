package io.games.poker_tournament_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.games.poker_tournament_tracker.domain.Player;
import io.games.poker_tournament_tracker.domain.SeasonPlayer;
import io.games.poker_tournament_tracker.model.PlayerDTO;
import io.games.poker_tournament_tracker.repos.PlayerRepository;
import io.games.poker_tournament_tracker.repos.SeasonPlayerRepository;
import io.games.poker_tournament_tracker.util.NotFoundException;
import io.games.poker_tournament_tracker.util.ReferencedWarning;

import lombok.extern.slf4j.Slf4j;

/** Service class for managing Players. */
@Service
@Slf4j
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;

  @Autowired
  public PlayerService(
      PlayerRepository playerRepository, SeasonPlayerRepository seasonPlayerRepository) {
    this.playerRepository = playerRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
  }

  /**
   * Retrieves all PlayerDTOs.
   *
   * @return a list of PlayerDTOs
   */
  public List<PlayerDTO> findAll() {
    try {
      log.info("Retrieving all players");
      final List<Player> players = playerRepository.findAll(Sort.by("playerId"));
      return players.stream().map(player -> mapToDTO(player, new PlayerDTO())).toList();
    } catch (Exception e) {
      log.error("Error finding all players", e);
      throw new RuntimeException("Failed to retrieve all players", e);
    }
  }

  /**
   * Retrieves the player ID by name.
   *
   * @param name the name of the player
   * @return the player ID
   */
  public int getPlayerIdByName(String name) {
    try {
      log.info("Retrieving player ID for name: {}", name);
      return playerRepository.findPlayerIdByName(name);
    } catch (Exception e) {
      log.error("Error retrieving player ID for name: {}", name, e);
      throw new RuntimeException("Failed to retrieve player ID for name: " + name, e);
    }
  }

  /**
   * Retrieves or creates a player ID by name.
   *
   * @param name the name of the player
   * @return the player ID
   */
  public Integer getOrCreatePlayerIdByName(String name) {
    try {
      log.info("Retrieving or creating player ID for name: {}", name);
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
                log.info("Created new player with ID: {}", playerId);
                return playerId;
              });
    } catch (Exception e) {
      log.error("Error retrieving or creating player ID for name: {}", name, e);
      throw new RuntimeException("Failed to retrieve or create player ID for name: " + name, e);
    }
  }

  /**
   * Retrieves a PlayerDTO by its ID.
   *
   * @param playerId the ID of the player
   * @return the PlayerDTO
   */
  public PlayerDTO get(final Integer playerId) {
    try {
      log.info("Retrieving player with id: {}", playerId);
      return playerRepository
          .findById(playerId)
          .map(player -> mapToDTO(player, new PlayerDTO()))
          .orElseThrow(() -> new NotFoundException("Player not found with id: " + playerId));
    } catch (Exception e) {
      log.error("Error getting player with id: {}", playerId, e);
      throw new RuntimeException("Failed to retrieve player with id: " + playerId, e);
    }
  }

  /**
   * Creates a new Player.
   *
   * @param playerDTO the DTO of the player to create
   * @return the ID of the created player
   */
  @Transactional
  public Integer create(final PlayerDTO playerDTO) {
    try {
      log.info("Creating new player");
      final Player player = new Player();
      mapToEntity(playerDTO, player);
      return playerRepository.save(player).getPlayerId();
    } catch (Exception e) {
      log.error("Error creating player", e);
      throw new RuntimeException("Failed to create player", e);
    }
  }

  /**
   * Updates an existing Player.
   *
   * @param playerId the ID of the player to update
   * @param playerDTO the DTO of the player to update
   */
  @Transactional
  public void update(final Integer playerId, final PlayerDTO playerDTO) {
    try {
      log.info("Updating player with id: {}", playerId);
      final Player player =
          playerRepository
              .findById(playerId)
              .orElseThrow(() -> new NotFoundException("Player not found with id: " + playerId));
      mapToEntity(playerDTO, player);
      playerRepository.save(player);
    } catch (Exception e) {
      log.error("Error updating player with id: {}", playerId, e);
      throw new RuntimeException("Failed to update player with id: " + playerId, e);
    }
  }

  /**
   * Deletes a Player by its ID.
   *
   * @param playerId the ID of the player to delete
   */
  @Transactional
  public void delete(final Integer playerId) {
    try {
      log.info("Deleting player with id: {}", playerId);
      playerRepository.deleteById(playerId);
    } catch (Exception e) {
      log.error("Error deleting player with id: {}", playerId, e);
      throw new RuntimeException("Failed to delete player with id: " + playerId, e);
    }
  }

  /**
   * Maps a Player entity to a PlayerDTO.
   *
   * @param player the Player entity
   * @param playerDTO the PlayerDTO
   * @return the mapped PlayerDTO
   */
  private PlayerDTO mapToDTO(final Player player, final PlayerDTO playerDTO) {
    playerDTO.setPlayerId(player.getPlayerId());
    playerDTO.setName(player.getName());
    playerDTO.setCreatedAt(player.getCreatedAt());
    return playerDTO;
  }

  /**
   * Maps a PlayerDTO to a Player entity.
   *
   * @param playerDTO the PlayerDTO
   * @param player the Player entity
   * @return the mapped Player entity
   */
  private Player mapToEntity(final PlayerDTO playerDTO, final Player player) {
    player.setName(playerDTO.getName());
    player.setCreatedAt(playerDTO.getCreatedAt());
    return player;
  }

  /**
   * Retrieves referenced warning for a player by its ID.
   *
   * @param playerId the ID of the player
   * @return the ReferencedWarning
   */
  public ReferencedWarning getReferencedWarning(final Integer playerId) {
    try {
      log.info("Retrieving referenced warning for player with id: {}", playerId);
      final ReferencedWarning referencedWarning = new ReferencedWarning();
      final Player player =
          playerRepository
              .findById(playerId)
              .orElseThrow(() -> new NotFoundException("Player not found with id: " + playerId));
      final SeasonPlayer playerSeasonPlayer = seasonPlayerRepository.findFirstByPlayer(player);
      if (playerSeasonPlayer != null) {
        referencedWarning.setKey("player.seasonPlayer.player.referenced");
        referencedWarning.addParam(playerSeasonPlayer.getSeasonPlayerId());
        return referencedWarning;
      }
      return null;
    } catch (Exception e) {
      log.error("Error retrieving referenced warning for player with id: {}", playerId, e);
      throw new RuntimeException(
          "Failed to retrieve referenced warning for player with id: " + playerId, e);
    }
  }
}
