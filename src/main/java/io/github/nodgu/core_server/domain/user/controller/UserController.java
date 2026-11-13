package io.github.nodgu.core_server.domain.user.controller;

import io.github.nodgu.core_server.domain.user.dto.LoginRequest;
import io.github.nodgu.core_server.domain.user.dto.LoginResponse;
import io.github.nodgu.core_server.domain.user.dto.UserUpdateRequest;
import io.github.nodgu.core_server.domain.user.dto.DeviceRequest;
import io.github.nodgu.core_server.domain.user.dto.DeviceResponse;
import io.github.nodgu.core_server.domain.user.dto.QuitRequest;
import io.github.nodgu.core_server.domain.user.dto.PasswordResetRequest;
import io.github.nodgu.core_server.domain.user.dto.PasswordResetConfirmRequest;
import io.github.nodgu.core_server.domain.user.entity.Device;
import io.github.nodgu.core_server.domain.user.entity.User;
import io.github.nodgu.core_server.domain.user.service.AccountDeletionService;
import io.github.nodgu.core_server.domain.user.service.UserService;
import io.github.nodgu.core_server.global.annotation.CurrentUser;
import io.github.nodgu.core_server.global.dto.ApiResponse;
import io.github.nodgu.core_server.global.util.JwtUtil;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AccountDeletionService accountDeletionService;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, AccountDeletionService accountDeletionService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.accountDeletionService = accountDeletionService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.findByEmail(loginRequest.getEmail());

            // 비밀번호 검증
            if (!userService.validatePassword(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("이메일 또는 비밀번호가 올바르지 않습니다", 401));
            }

            // 이용 정지된 계정은 로그인 불가 (isActive는 탈퇴가 아닌 정지 용도)
            if (!Boolean.TRUE.equals(user.getIsActive())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("이용이 제한된 계정입니다", 403));
            }

            // 실제 JWT 토큰 생성
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname());

            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken,
                    userInfo);

            return ResponseEntity.ok(ApiResponse.success("로그인 성공", response, 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("로그인 실패: " + e.getMessage(), 400));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody LoginRequest registerRequest) {
        try {
            User user = userService.createUser(
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    "사용자" // 임시 닉네임
            );

            return ResponseEntity.ok(ApiResponse.success("회원가입 성공", "사용자 ID: " + user.getId(), 201));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("회원가입 실패: " + e.getMessage(), 400));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("유효하지 않은 토큰입니다", 401));
            }

            String refreshToken = authHeader.substring(7);

            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("만료된 토큰입니다", 401));
            }

            String userEmail = jwtUtil.extractEmail(refreshToken);
            User user = userService.findByEmail(userEmail);

            // 탈퇴 후 같은 이메일로 재가입한 계정을 예전 토큰으로 갱신하지 못하게 userId까지 확인
            if (!user.getId().equals(jwtUtil.extractUserId(refreshToken))
                    || !Boolean.TRUE.equals(user.getIsActive())
                    || jwtUtil.isIssuedBeforePasswordChange(refreshToken, user)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("유효하지 않은 토큰입니다", 401));
            }

            // 새로운 토큰 생성
            String newAccessToken = jwtUtil.generateAccessToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname());

            LoginResponse response = new LoginResponse(
                    newAccessToken,
                    newRefreshToken,
                    userInfo);

            return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response, 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("토큰 갱신 실패: " + e.getMessage(), 400));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> getCurrentUser(@CurrentUser User user) {
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getNickname());

        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", userInfo, 200));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<LoginResponse.UserInfo>> updateCurrentUser(
            @CurrentUser User user,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        User updatedUser = userService.updateUser(user, updateRequest.getNickname(), updateRequest.getPassword());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getNickname());
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 수정 성공", userInfo, 200));
    }

    @PostMapping("/quit")
    public ResponseEntity<ApiResponse<Void>> quitUser(@CurrentUser User user,
            @Valid @RequestBody QuitRequest quitRequest) {
        // 비밀번호 확인
        if (!userService.validatePassword(quitRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("비밀번호가 올바르지 않습니다", 400));
        }

        // 계정과 스크랩·키워드·알림 설정·FCM 토큰 등 관련 데이터를 한 트랜잭션으로 실제 삭제
        accountDeletionService.deleteAccount(user.getId());

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다", null, 200));
    }

    @PostMapping("/device")
    public ResponseEntity<ApiResponse<DeviceResponse>> registerDevice(@CurrentUser User user,
            @Valid @RequestBody DeviceRequest deviceRequest) {
        Device registeredDevice = userService.registerDevice(user, deviceRequest.getFcmToken());
        DeviceResponse response = new DeviceResponse(registeredDevice);
        return ResponseEntity.ok(ApiResponse.success("기기 등록 성공", response, 200));
    }

    @DeleteMapping("/device")
    public ResponseEntity<ApiResponse<Void>> unregisterDevice(@CurrentUser User user,
            @Valid @RequestBody DeviceRequest deviceRequest) {
        userService.unregisterDevice(user, deviceRequest.getFcmToken());
        return ResponseEntity.ok(ApiResponse.success("기기 등록 해제 성공", null, 200));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        // 가입 여부가 드러나지 않도록 결과와 상관없이 항상 같은 성공 응답을 반환한다
        try {
            userService.requestPasswordReset(request.getEmail());
        } catch (Exception e) {
            log.error("비밀번호 재설정 메일 처리 실패", e);
        }
        return ResponseEntity.ok(ApiResponse.success(
                "가입된 이메일이라면 비밀번호 재설정 메일이 발송됩니다", null, 200));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다", null, 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("비밀번호 변경 실패: " + e.getMessage(), 400));
        }
    }
}