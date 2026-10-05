package com.skipps.gpu_farm_manager.user;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.gpu_farm_manager.user.dto.CreateUserRequest;
import com.skipps.gpu_farm_manager.user.dto.UpdatePasswordRequest;
import com.skipps.gpu_farm_manager.user.dto.UpdateRoleRequest;
import com.skipps.gpu_farm_manager.user.dto.UpdateUsernameRequest;
import com.skipps.gpu_farm_manager.user.dto.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserModel currentUser) {
        return ResponseEntity.ok(userService.getUserProfile(currentUser));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #id == #currentUser.id")
    public ResponseEntity<UserResponse> getUser(
        @PathVariable Long id,
        @AuthenticationPrincipal UserModel currentUser
    ) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}/username")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #id == #currentUser.id")
    public ResponseEntity<UserResponse> updateUsername(
        @PathVariable Long id,
        @RequestBody @Valid UpdateUsernameRequest request,
        @AuthenticationPrincipal UserModel currentUser
    ) {
        return ResponseEntity.ok(userService.updateUsername(id, request, currentUser));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMINISTRATOR') or #id == #currentUser.id")
    public ResponseEntity<UserResponse> updatePassword(
        @PathVariable Long id,
        @RequestBody @Valid UpdatePasswordRequest request,
        @AuthenticationPrincipal UserModel currentUser
    ) {
        return ResponseEntity.ok(userService.updatePassword(id, request, currentUser));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<UserResponse> updateRole(
        @PathVariable Long id,
        @RequestBody @Valid UpdateRoleRequest request
    ) {
        return ResponseEntity.ok(userService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteUser(
        @PathVariable Long id,
        @AuthenticationPrincipal UserModel currentUser
    ) {
        userService.deleteUser(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
