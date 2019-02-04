package com.greenfox.tribes1.Troop;

import com.greenfox.tribes1.Resources.Food;
import com.greenfox.tribes1.Troop.Model.Troop;

public class TroopFactory {
  
  public Troop makeTroop(TroopType troopType) {
    
    troopType.makeTroop()
        .getKingdom()
        .getResources()
        .stream()
        .filter(resource -> resource instanceof Food)
        .filter(food -> food.getAmount() > 0)
        .forEach(x -> x.setAmountPerMinute(x.getAmountPerMinute() - 1));
    
    return troopType.makeTroop();
  }
}