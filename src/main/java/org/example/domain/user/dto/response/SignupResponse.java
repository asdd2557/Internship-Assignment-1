package org.example.domain.user.dto.response;

import org.example.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class SignupResponse {
  private Long id;
  private String username;
  private String nickname;
  private List<RoleResponse> roles;

  public SignupResponse(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.nickname = user.getNickname();
    this.roles = user.getRolesAsString().stream()
        .map(RoleResponse::new)
        .collect(Collectors.toList());
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getNickname() {
    return nickname;
  }

  public List<RoleResponse> getRoles() {
    return roles;
  }

  public static class RoleResponse {
    private String role;

    public RoleResponse(String role) {
      this.role = role;
    }

    public String getRole() {
      return role;
    }
  }
}
