package org.example.security;

import org.example.domain.user.dto.request.LoginRequest;
import org.example.domain.user.dto.request.SignupRequest;
import org.example.domain.user.dto.response.SignupResponse;
import org.example.domain.user.entity.Role;
import org.example.domain.user.entity.User;
import org.example.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public SignupResponse signup(SignupRequest req) {
    if (userRepository.existsByUsername(req.getUsername())) {
      throw new RuntimeException("USER_ALREADY_EXISTS");
    }
    User user = new User(req.getUsername(), passwordEncoder.encode(req.getPassword()), req.getNickname(), new HashSet<>(Set.of(Role.USER)));
    userRepository.save(user);
    return new SignupResponse(user);
  }

  public TokenResponse login(LoginRequest req) {
    User user = userRepository.findByUsername(req.getUsername())
        .orElseThrow(() -> new RuntimeException("INVALID_CREDENTIALS"));

    if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
      throw new RuntimeException("INVALID_CREDENTIALS");
    }

    String token = jwtTokenProvider.createToken(user.getUsername(), user.getRolesAsString());
    return new TokenResponse(token);
  }

  public SignupResponse grantAdminRole(Long userId, String requesterName) {
    User requester = userRepository.findByUsername(requesterName).orElseThrow();
    if (!requester.getRoles().contains(Role.ADMIN)) {
      throw new RuntimeException("ACCESS_DENIED");
    }

    User user = userRepository.findById(userId).orElseThrow();
    user.getRoles().add(Role.ADMIN);
    return new SignupResponse(user);
  }
}
