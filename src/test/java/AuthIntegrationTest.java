package org.example;

import org.example.domain.user.dto.request.LoginRequest;
import org.example.domain.user.dto.request.SignupRequest;
import org.example.domain.user.dto.response.SignupResponse;
import org.example.domain.user.entity.Role;
import org.example.domain.user.entity.User;
import org.example.domain.user.repository.UserRepository;
import org.example.security.AuthService;
import org.example.security.JwtTokenProvider;
import org.example.security.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
public class AuthIntegrationTest {

  @Autowired
  private AuthService authService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  private final String encodedPassword = "$2a$10$mockencodedpassword1234567890abcdef";

  @BeforeEach
  void setup() {
    // 기본 유저 등록 상태 정의
    given(userRepository.existsByUsername("dupeuser")).willReturn(true);
    given(userRepository.findByUsername("nouser")).willReturn(Optional.empty());

    User loginUser = new User(1L, "loginuser", encodedPassword, "Login", Set.of(Role.USER));
    given(userRepository.findByUsername("loginuser")).willReturn(Optional.of(loginUser));
    given(userRepository.existsByUsername("loginuser")).willReturn(false);
    given(passwordEncoder.matches("pw1234", encodedPassword)).willReturn(true);

    given(jwtTokenProvider.createToken(eq("loginuser"), any())).willReturn("mock.jwt.token");
    given(userRepository.findById(2L)).willReturn(Optional.of(
        new User(2L, "target", encodedPassword, "Target", Set.of(Role.USER))));
    given(userRepository.findByUsername("normal"))
        .willReturn(Optional.of(new User(3L, "normal", encodedPassword, "Norm", Set.of(Role.USER))));
  }

  @Test
  void test_회원가입_성공() {
    SignupRequest request = new SignupRequest();
    request.setUsername("testuser");
    request.setPassword("testpass");
    request.setNickname("Tester");

    given(userRepository.existsByUsername("testuser")).willReturn(false);
    SignupResponse response = authService.signup(request);

    assertEquals("testuser", response.getUsername());
    assertEquals("Tester", response.getNickname());
    assertTrue(response.getRoles().stream().anyMatch(role -> role.getRole().equals(Role.USER.name())));
  }

  @Test
  void test_회원가입_중복() {
    SignupRequest request = new SignupRequest();
    request.setUsername("dupeuser");
    request.setPassword("1234");
    request.setNickname("Dup");

    RuntimeException e = assertThrows(RuntimeException.class, () -> authService.signup(request));
    assertEquals("USER_ALREADY_EXISTS", e.getMessage());
  }

  @Test
  void test_로그인_성공() {
    LoginRequest login = new LoginRequest();
    login.setUsername("loginuser");
    login.setPassword("pw1234");

    TokenResponse tokenResponse = authService.login(login);
    assertNotNull(tokenResponse.getToken());
  }

  @Test
  void test_로그인_실패() {
    LoginRequest login = new LoginRequest();
    login.setUsername("nouser");
    login.setPassword("nopass");

    RuntimeException e = assertThrows(RuntimeException.class, () -> authService.login(login));
    assertEquals("INVALID_CREDENTIALS", e.getMessage());
  }

  @Test
  void test_관리자_권한_부여_실패_일반유저() {
    RuntimeException e = assertThrows(RuntimeException.class,
        () -> authService.grantAdminRole(2L, "normal"));

    assertEquals("ACCESS_DENIED", e.getMessage());
  }
}
