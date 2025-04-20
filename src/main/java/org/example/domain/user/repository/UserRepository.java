package org.example.domain.user.repository;

import org.example.domain.user.entity.Role;
import org.example.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
  private final Map<Long, User> storage = new ConcurrentHashMap<>();
  private final Map<String, User> byUsername = new ConcurrentHashMap<>();
  private long idCounter = 1L;

  public UserRepository() {
    // 최초 관리자 계정 초기화
    User admin = new User();
    admin.setId(idCounter++);
    admin.setUsername("admin");
    admin.setPassword("$2a$10$fC0PzbOdzcXwI6mAW32hcuxciIu7zlaRTyWvoabWKKakf.6CHeZI."); // 비밀번호는 암호화된 값으로 덮어쓰기 필요
    admin.setNickname("SuperAdmin");
    admin.setRoles(java.util.Set.of(Role.ADMIN));

    storage.put(admin.getId(), admin);
    byUsername.put(admin.getUsername(), admin);
  }

  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(byUsername.get(username));
  }

  public Optional<User> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public void save(User user) {
    if (user.getId() == null) {
      user.setId(idCounter++);
    }
    storage.put(user.getId(), user);
    byUsername.put(user.getUsername(), user);
  }

  public boolean existsByUsername(String username) {
    return byUsername.containsKey(username);
  }
}
