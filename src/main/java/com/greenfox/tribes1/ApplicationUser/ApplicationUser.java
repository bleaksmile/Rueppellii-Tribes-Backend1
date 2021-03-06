package com.greenfox.tribes1.ApplicationUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greenfox.tribes1.Kingdom.Kingdom;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplicationUser {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;
  @JsonIgnore
  private String password;
  private String userEmail;
  
  @OneToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "kingdom_id")
  private Kingdom kingdom;
  
  public ApplicationUser(String username, String password, String userEmail) {
    this.username = username;
    this.password = password;
    this.userEmail = userEmail;
  }
}
