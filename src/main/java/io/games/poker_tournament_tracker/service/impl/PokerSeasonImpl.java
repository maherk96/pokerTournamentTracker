package io.games.poker_tournament_tracker.service.impl;

import io.games.poker_tournament_tracker.model.SeasonDTO;
import io.games.poker_tournament_tracker.service.SeasonService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PokerSeasonImpl {

  @Autowired private SeasonService seasonService;

  // Fields to Populate:
  //
  //	•	name: The name of the season (e.g., “Spring 2024 Tournament”).
  //	•	start_date: The date when the season begins.
  //	•	end_date: Optional initially. Set when the season concludes.
  //	•	created_at: Automatically populated. Timestamp when the season record is created.
  //
  // Steps:
  //
  //	1.	Insert a New Record into seasons:
  //	•	Required Fields: name, start_date.
  //	•	Optional Field: end_date can be left NULL until the season ends.
  //	•	Example Data:
  //	•	name: “Spring 2024 Tournament”
  //	•	start_date: “2024-05-01”

  //	•	season_players
  //
  // Fields to Populate:
  //
  //	•	season_id: Reference to the season_id from the seasons table.
  //	•	player_id: Reference to each player’s player_id from the players table.
  //	•	allocated_pot_size: The total pot size allocated to the player at the start of the season
  // (e.g., $1,000.00).
  //	•	min_buy_in: The minimum buy-in required for games in the season (e.g., $50.00).
  //	•	current_pot_size: Initially set equal to allocated_pot_size.
  public void createSeason(String seasonName) {
    SeasonDTO seasonDTO = new SeasonDTO();

    seasonDTO.setName(seasonName);
    seasonDTO.setStartDate(LocalDate.now());

    seasonService.create(seasonDTO);
  }
}
