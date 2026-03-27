package com.affidock.api.modules.users.controller;

import com.affidock.api.common.base.BaseController;
import com.affidock.api.modules.users.dto.AvatarUploadRequest;
import com.affidock.api.modules.users.dto.ChangePasswordRequest;
import com.affidock.api.modules.users.dto.UpdateUserProfileRequest;
import com.affidock.api.modules.users.dto.UserProfileResponse;
import com.affidock.api.modules.users.dto.UserRequest;
import com.affidock.api.modules.users.dto.UserResponse;
import com.affidock.api.modules.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController extends BaseController<UserRequest, UserResponse> {
    private final UserService userService;

    public UserController(UserService service) {
        super(service);
        this.userService = service;
    }

    @PatchMapping("/me/password/change")
    public ResponseEntity<Void> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changeAuthenticatedUserPassword(request.currentPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getAuthenticatedUserProfile());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userService.updateAuthenticatedUserProfile(request));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<UserProfileResponse> uploadMyAvatar(@Valid @RequestBody AvatarUploadRequest request) {
        return ResponseEntity.ok(userService.uploadAuthenticatedUserAvatar(request.base64Data(), request.mimeType(), request.fileName()));
    }

    @DeleteMapping("/me/avatar")
    public ResponseEntity<UserProfileResponse> deleteMyAvatar() {
        return ResponseEntity.ok(userService.deleteAuthenticatedUserAvatar());
    }

    @GetMapping("/me/avatar")
    public ResponseEntity<byte[]> getMyAvatar() {
        UserService.AvatarBinaryData avatar = userService.getAuthenticatedUserAvatarBinary();
        MediaType mediaType = MediaType.parseMediaType(avatar.mimeType());
        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-store, max-age=0")
            .contentType(mediaType)
            .body(avatar.bytes());
    }
}
