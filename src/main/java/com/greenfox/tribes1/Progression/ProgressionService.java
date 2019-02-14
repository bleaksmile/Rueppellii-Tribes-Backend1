package com.greenfox.tribes1.Progression;

import com.greenfox.tribes1.Building.Building;
import com.greenfox.tribes1.Building.BuildingFactory;
import com.greenfox.tribes1.Building.BuildingService;
import com.greenfox.tribes1.Building.BuildingType;
import com.greenfox.tribes1.Exception.*;
import com.greenfox.tribes1.Kingdom.Kingdom;
import com.greenfox.tribes1.Kingdom.KingdomService;
import com.greenfox.tribes1.Progression.DTO.ProgressionDTO;
import com.greenfox.tribes1.TimeService;
import com.greenfox.tribes1.Troop.Model.Troop;
import com.greenfox.tribes1.Troop.TroopFactory;
import com.greenfox.tribes1.Troop.TroopService;
import com.greenfox.tribes1.Troop.TroopType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressionService {

  private ProgressionRepository progressionRepository;
  private TimeService timeService;
  private KingdomService kingdomService;
  private BuildingService buildingService;
  private TroopService troopService;

  private Long level = 0L;

  //Comment
  @Autowired
  public ProgressionService(ProgressionRepository progressionRepository, TimeService timeService, KingdomService kingdomService, BuildingService buildingService, TroopService troopService) {
    this.progressionRepository = progressionRepository;
    this.timeService = timeService;
    this.kingdomService = kingdomService;
    this.buildingService = buildingService;
    this.troopService = troopService;
  }

  public void saveProgression(ProgressionDTO progressionDTO) {
    Progression progressionToSave = createProgressionFromDTO(progressionDTO);
    progressionToSave.setFinished(timeService.calculateBuildingTimeForNewBuildingOrTroop(progressionToSave));
    progressionRepository.save(progressionToSave);
  }

  public Progression createProgressionFromDTO(ProgressionDTO progressionDTO) {
    ModelMapper modelMapper = new ModelMapper();
    return modelMapper.map(progressionDTO, Progression.class);
  }

  public void safeDeleteAllProgressionsWithExpiredTimestamp() {
    List<Progression> allExpired = listOfAllProgressionsWithExpiredTimestamp();
    for (Progression expired : allExpired) {
      expired.setKingdom(null);
      progressionRepository.delete(expired);
    }
  }

  public List<Progression> listOfThingsToCreateWithExpiredTimestamp(String type) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    return progressionRepository.findByTypeAndFinishedIsLessThanAndLevelEquals(type, currentTime, level);
  }

  public List<Progression> listOfThingsToUpgradeeWithExpiredTimestamp(String type) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    return progressionRepository.findByTypeAndFinishedIsLessThanAndLevelGreaterThan(type, currentTime, level);
  }

  public List<Progression> listOfAllProgressionsWithExpiredTimestamp() {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    return progressionRepository.findByFinishedLessThan(currentTime);
  }

  public void checkConstruction() throws NotValidKingdomNameException, TroopIdNotFoundException, BuildingIdNotFoundException, NotValidTypeException, BuildingNotValidException, TroopNotValidException {
    finishMineConstructions();
    finishFarmConstructions();
    finishBarracksConstructions();
    finishTroopConstructions();
    finishMineUpgrade();
    finishFarmUpgrade();
    finishBarracksUpgrade();
    finishTroopUpgrade();
    safeDeleteAllProgressionsWithExpiredTimestamp();
  }

  public void finishMineConstructions() throws NotValidKingdomNameException, BuildingNotValidException, BuildingIdNotFoundException {
    List<Progression> mines = listOfThingsToCreateWithExpiredTimestamp("mine");
    for (Progression mine : mines) {
      addBuildingToKingdom(mine, createNewBuilding(mine));
    }
  }

  public void finishFarmConstructions() throws NotValidKingdomNameException, BuildingNotValidException, BuildingIdNotFoundException {
    List<Progression> farms = listOfThingsToCreateWithExpiredTimestamp("farm");
    for (Progression farm : farms) {
      addBuildingToKingdom(farm, createNewBuilding(farm));
    }
  }

  public void finishBarracksConstructions() throws NotValidKingdomNameException, BuildingNotValidException, BuildingIdNotFoundException {
    List<Progression> barracks = listOfThingsToCreateWithExpiredTimestamp("barracks");
    for (Progression barrack : barracks) {
      addBuildingToKingdom(barrack, createNewBuilding(barrack));
    }
  }

  public void finishTroopConstructions() throws NotValidKingdomNameException, TroopNotValidException {
    List<Progression> troops = listOfThingsToCreateWithExpiredTimestamp("troop");
    for (Progression troop : troops) {
      addTroopToKingdom(troop, createNewTroop(troop));
    }
  }

  public void finishMineUpgrade() throws TroopIdNotFoundException, BuildingNotValidException, NotValidTypeException, BuildingIdNotFoundException {
    List<Progression> mines = listOfThingsToUpgradeeWithExpiredTimestamp("mine");
    for (Progression mine : mines) {
      upgradeMine(mine);
    }
  }

  public void finishFarmUpgrade() throws TroopIdNotFoundException, BuildingNotValidException, NotValidTypeException, BuildingIdNotFoundException {
    List<Progression> farms = listOfThingsToUpgradeeWithExpiredTimestamp("farm");
    for (Progression farm : farms) {
      upgradeFarm(farm);
    }
  }

  public void finishBarracksUpgrade() throws TroopIdNotFoundException, BuildingNotValidException, NotValidTypeException, BuildingIdNotFoundException {
    List<Progression> barracks = listOfThingsToUpgradeeWithExpiredTimestamp("barracks");
    for (Progression barrack : barracks) {
      upgradeBarracks(barrack);
    }
  }

  public void finishTroopUpgrade() throws TroopIdNotFoundException, NotValidTypeException, BuildingIdNotFoundException, TroopNotValidException {
    List<Progression> troops = listOfThingsToUpgradeeWithExpiredTimestamp("troop");
    for (Progression troop : troops) {
      upgradeTroop(troop);
    }
  }

  public Building createNewBuilding(Progression progression) {
    String type = progression.getType();
    return BuildingFactory.createBuilding(BuildingType.valueOf(type));
  }

  public Troop createNewTroop(Progression progression) {
    String type = progression.getType();
    return TroopFactory.createTroop(TroopType.valueOf(type));
  }

  public void upgradeMine(Progression progression) throws NotValidTypeException, TroopIdNotFoundException, BuildingIdNotFoundException, BuildingNotValidException {
    Building buildingToUpgrade = (Building) getExactBuildingOrTroop_FromProgressionModelId(progression);
    buildingService.upgradeMine(buildingToUpgrade);
  }

  public void upgradeFarm(Progression progression) throws NotValidTypeException, TroopIdNotFoundException, BuildingIdNotFoundException, BuildingNotValidException {
    Building buildingToUpgrade = (Building) getExactBuildingOrTroop_FromProgressionModelId(progression);
    buildingService.upgradeFarm(buildingToUpgrade);
  }

  public void upgradeBarracks(Progression progression) throws NotValidTypeException, TroopIdNotFoundException, BuildingIdNotFoundException, BuildingNotValidException {
    Building buildingToUpgrade = (Building) getExactBuildingOrTroop_FromProgressionModelId(progression);
    buildingService.upgradeBarracks(buildingToUpgrade);
  }

  public void upgradeTroop(Progression progression) throws NotValidTypeException, TroopIdNotFoundException, BuildingIdNotFoundException, TroopNotValidException {
    Troop troopToUpgrade = (Troop) getExactBuildingOrTroop_FromProgressionModelId((progression));
    troopService.upgradeTroop(troopToUpgrade);
  }

  public Progression findById(Long id) throws ProgressionIdNotFoundException {
    return Optional.of(progressionRepository.findById(id)).get().orElseThrow(()
            -> new ProgressionIdNotFoundException(("There is no Troop with such Id")));
  }

  public Boolean isTypeBuilding(Progression progression) {
    return (progression.getType().equals("barracks") ||
            progression.getType().equals("farm") ||
            progression.getType().equals("mine"));
  }

  public Boolean isTypeTroop(Progression progression) {
    return progression.getType().equals("troop");
  }

  public Object getExactBuildingOrTroop_FromProgressionModelId(Progression progression) throws BuildingIdNotFoundException, TroopIdNotFoundException, NotValidTypeException {
    if (!isTypeBuilding(progression) || (!isTypeTroop(progression))) {
      throw new NotValidTypeException("Invalid Troop or Building Type");
    } else if (isTypeBuilding(progression)) {
      return buildingService.findById(progression.getModel_id());
    }
    return troopService.findById(progression.getModel_id());
  }

  public void addBuildingToKingdom(Progression progression, Building newBuilding) throws NotValidKingdomNameException, BuildingNotValidException, BuildingIdNotFoundException {
    Kingdom kingdomAddTo = progression.getKingdom();
    List<Building> buildingsOfKingdom = kingdomAddTo.getBuildings();
    buildingsOfKingdom.add(newBuilding);
    newBuilding.setKingdom(kingdomAddTo);
    kingdomAddTo.setBuildings(buildingsOfKingdom);
    buildingService.saveBuilding(newBuilding);
  }

  public void addTroopToKingdom(Progression progression, Troop newTroop) throws NotValidKingdomNameException, TroopNotValidException {
    Kingdom kingdomAddTo = progression.getKingdom();
    List<Troop> troopsOfKingdom = kingdomAddTo.getTroops();

    newTroop.setKingdom(kingdomAddTo);
    troopsOfKingdom.add(newTroop);
    kingdomAddTo.setTroops(troopsOfKingdom);
    troopService.save(newTroop);
  }
}
