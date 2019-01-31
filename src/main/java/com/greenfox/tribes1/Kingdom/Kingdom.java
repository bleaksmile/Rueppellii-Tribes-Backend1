package com.greenfox.tribes1.Kingdom;

import lombok.*;
import com.greenfox.tribes1.ApplicationUser.ApplicationUser;
import com.greenfox.tribes1.Troop.Troop;
import com.greenfox.tribes1.Building.Building;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Kingdom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
//    private Long userId;
//    List<Resource> resources;
//    Location location;

  @OneToOne(mappedBy = "kingdom")
  ApplicationUser applicationUser;

  @OneToMany
  private List<Troop> troops;

  List<Building> buildings;

  public Kingdom(String name) {
    this.name = name;
  }
}
