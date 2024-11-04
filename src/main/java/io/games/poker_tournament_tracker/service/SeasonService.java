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
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SeasonService {

  private final SeasonRepository seasonRepository;
  private final SeasonPlayerRepository seasonPlayerRepository;
  private final GameRepository gameRepository;

  public SeasonService(
      final SeasonRepository seasonRepository,
      final SeasonPlayerRepository seasonPlayerRepository,
      final GameRepository gameRepository) {
    this.seasonRepository = seasonRepository;
    this.seasonPlayerRepository = seasonPlayerRepository;
    this.gameRepository = gameRepository;
  }

  public List<SeasonDTO> findAll() {
    final List<Season> seasons = seasonRepository.findAll(Sort.by("seasonId"));
    return seasons.stream().map(season -> mapToDTO(season, new SeasonDTO())).toList();
  }

  public int getSeasonIdByName(String name) {
    return seasonRepository.findSeasonIdByName(name);
  }

  public SeasonDTO get(final Integer seasonId) {
    return seasonRepository
        .findById(seasonId)
        .map(season -> mapToDTO(season, new SeasonDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public Integer create(final SeasonDTO seasonDTO) {
    final Season season = new Season();
    mapToEntity(seasonDTO, season);
    return seasonRepository.save(season).getSeasonId();
  }

  public void update(final Integer seasonId, final SeasonDTO seasonDTO) {
    final Season season = seasonRepository.findById(seasonId).orElseThrow(NotFoundException::new);
    mapToEntity(seasonDTO, season);
    seasonRepository.save(season);
  }

  public void delete(final Integer seasonId) {
    seasonRepository.deleteById(seasonId);
  }

  private SeasonDTO mapToDTO(final Season season, final SeasonDTO seasonDTO) {
    seasonDTO.setSeasonId(season.getSeasonId());
    seasonDTO.setName(season.getName());
    seasonDTO.setStartDate(season.getStartDate());
    seasonDTO.setEndDate(season.getEndDate());
    seasonDTO.setCreatedAt(season.getCreatedAt());
    return seasonDTO;
  }

  private Season mapToEntity(final SeasonDTO seasonDTO, final Season season) {
    season.setName(seasonDTO.getName());
    season.setStartDate(seasonDTO.getStartDate());
    season.setEndDate(seasonDTO.getEndDate());
    season.setCreatedAt(seasonDTO.getCreatedAt());
    return season;
  }

  public ReferencedWarning getReferencedWarning(final Integer seasonId) {
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
  }
}
