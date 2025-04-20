package org.example.security;

import org.example.domain.user.dto.request.LoginRequest;
import org.example.domain.user.dto.request.SignupRequest;
import org.example.domain.user.dto.response.SignupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
    try {
      return ResponseEntity.ok(authService.signup(request));
    } catch (RuntimeException e) {
      if (e.getMessage().equals("USER_ALREADY_EXISTS")) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error("USER_ALREADY_EXISTS", "이미 가입된 사용자입니다."));
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("UNKNOWN_ERROR", e.getMessage()));
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      return ResponseEntity.ok(authService.login(request));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."));
    }
  }

  @PatchMapping("/admin/users/{userId}/roles")
  public ResponseEntity<?> grantAdmin(@PathVariable Long userId,
                                      @AuthenticationPrincipal UserDetails userDetails) {
    try {
      return ResponseEntity.ok(authService.grantAdminRole(userId, userDetails.getUsername()));
    } catch (RuntimeException e) {
      if (e.getMessage().equals("ACCESS_DENIED")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error("ACCESS_DENIED", "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."));
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("USER_NOT_FOUND", "존재하지 않는 사용자입니다."));
    }
  }

  private Map<String, Object> error(String code, String message) {
    Map<String, Object> error = new HashMap<>();
    error.put("error", Map.of("code", code, "message", message));
    return error;
  }
}
