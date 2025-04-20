package org.example.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private Long id;
  private String username;
  private String password;
  private String nickname;
  private Set<Role> roles;

  public User(String username, String password, String nickname, Set<Role> roles) {
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.roles = roles;
  }


  public java.util.List<String> getRolesAsString() {
    return roles.stream().map(role -> role.name()).collect(Collectors.toList());
  }
}
