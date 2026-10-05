package com.skipps.gpu_farm_manager.user;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.exception.BadRequestException;
import com.skipps.gpu_farm_manager.exception.UserAlreadyExistsException;
import com.skipps.gpu_farm_manager.exception.UserNotFoundException;
import com.skipps.gpu_farm_manager.user.dto.CreateUserRequest;
import com.skipps.gpu_farm_manager.user.dto.UpdatePasswordRequest;
import com.skipps.gpu_farm_manager.user.dto.UpdateRoleRequest;
import com.skipps.gpu_farm_manager.user.dto.UpdateUsernameRequest;
import com.skipps.gpu_farm_manager.user.dto.UserResponse;

@Service
@Transactional
public class UserService
{
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request)
    {
        if (userRepository.existsByUsername(request.username()))
        {
            throw UserAlreadyExistsException.withUsername(request.username());
        }

        UserModel user = UserModel.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password()))
            .role(request.role() != null ? request.role() : Role.USER)
            .createdAt(LocalDateTime.now())
            .build();

        user = userRepository.save(user);

        log.info("Created user '{}' [ID: {}] with role {}", user.getUsername(), user.getId(), user.getRole());

        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id)
    {
        UserModel user = userRepository.findById(id)
            .orElseThrow(() -> UserNotFoundException.withId(id));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username)
    {
        UserModel user = userRepository.findByUsername(username)
            .orElseThrow(() -> UserNotFoundException.withUsername(username));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers()
    {
        return userRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(UserModel currentUser)
    {
        if (currentUser == null)
        {
            throw new BadRequestException("No authenticated user in current session");
        }
        return getUser(currentUser.getId());
    }

    public UserResponse updateUsername(Long id, UpdateUsernameRequest request, UserModel currentUser)
    {
        UserModel user = userRepository.findById(id)
            .orElseThrow(() -> UserNotFoundException.withId(id));

        validateOwnershipOrAdmin(user, currentUser);

        if (!user.getUsername().equals(request.newUsername()))
        {
            if (userRepository.existsByUsername(request.newUsername()))
            {
                throw UserAlreadyExistsException.withUsername(request.newUsername());
            }

            String oldUsername = user.getUsername();
            user.setUsername(request.newUsername());
            user = userRepository.save(user);

            log.info("Updated username for user [ID: {}] from '{}' to '{}'", user.getId(), oldUsername, user.getUsername());
        }

        return mapToResponse(user);
    }

    public UserResponse updatePassword(Long id, UpdatePasswordRequest request, UserModel currentUser)
    {
        UserModel user = userRepository.findById(id)
            .orElseThrow(() -> UserNotFoundException.withId(id));

        validateOwnershipOrAdmin(user, currentUser);

        boolean isSelf = currentUser != null && user.getId().equals(currentUser.getId());
        boolean isAdmin = currentUser != null && currentUser.getRole() == Role.ADMINISTRATOR;

        // When updating own password or non-admin, current password verification is required
        if (isSelf || !isAdmin)
        {
            if (request.currentPassword() == null || request.currentPassword().isBlank())
            {
                throw new BadRequestException("Current password is required to change password");
            }
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword()))
            {
                throw new BadRequestException("Current password does not match");
            }
        }
        else if (request.currentPassword() != null && !request.currentPassword().isBlank())
        {
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword()))
            {
                throw new BadRequestException("Current password does not match");
            }
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user = userRepository.save(user);

        log.info("Updated password for user [ID: {}] ('{}')", user.getId(), user.getUsername());

        return mapToResponse(user);
    }

    public UserResponse updateRole(Long id, UpdateRoleRequest request)
    {
        UserModel user = userRepository.findById(id)
            .orElseThrow(() -> UserNotFoundException.withId(id));

        user.setRole(request.role());
        user = userRepository.save(user);

        log.info("Updated role for user [ID: {}] ('{}') to {}", user.getId(), user.getUsername(), user.getRole());

        return mapToResponse(user);
    }

    public void deleteUser(Long id, UserModel currentUser)
    {
        UserModel user = userRepository.findById(id)
            .orElseThrow(() -> UserNotFoundException.withId(id));

        if (currentUser != null && user.getId().equals(currentUser.getId()))
        {
            throw new BadRequestException("You cannot delete your own user account");
        }

        userRepository.delete(user);
        log.info("Deleted user [ID: {}] ('{}')", id, user.getUsername());
    }

    private void validateOwnershipOrAdmin(UserModel targetUser, UserModel currentUser)
    {
        if (currentUser == null)
        {
            throw new AccessDeniedException("Authentication required");
        }

        boolean isSelf = targetUser.getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMINISTRATOR;

        if (!isSelf && !isAdmin)
        {
            throw new AccessDeniedException("You are not authorized to modify this user account");
        }
    }

    public UserResponse mapToResponse(UserModel user)
    {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getRole() != null ? user.getRole().name() : null,
            user.getCreatedAt()
        );
    }
}
