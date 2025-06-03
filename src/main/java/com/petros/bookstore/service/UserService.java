package com.petros.bookstore.service;

import com.petros.bookstore.dto.UserDTO.UserAdminUpdateRequestDto;
import com.petros.bookstore.dto.UserDTO.UserProfileResponseDto;
import com.petros.bookstore.dto.UserDTO.UserProfileUpdateRequestDto;
import com.petros.bookstore.exception.customException.ResourceGoneException;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling User functionality,
 * including profile updates, administrative updates, user search, and deletion.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user
     * @return the found {@link User}
     * @throws ResourceGoneException if the user account is deleted
     */
    public User getUserByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceGoneException("The user account has been deleted"));
    }

    /**
     * Updates the profile of a user.
     *
     * @param userId the ID of the user to update
     * @param updateRequest the profile update request data
     * @return the updated {@link User}
     * @throws ResourceNotFoundException if the user does not exist
     */
    public User updateUserProfile(final Long userId, UserProfileUpdateRequestDto updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found."));

        if (updateRequest.firstName() != null) {
            user.setFirstName(updateRequest.firstName());
        }
        if (updateRequest.lastName() != null) {
            user.setLastName(updateRequest.lastName());
        }
        if (updateRequest.username() != null) {
            user.setUsername(updateRequest.username());
        }
        if (updateRequest.password() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.password()));
        }

        return userRepository.save(user);
    }

    /**
     * Searches users based on optional username, first name, and last name filters.
     *
     * @param username optional username filter
     * @param firstName optional first name filter
     * @param lastName optional last name filter
     * @param pageable the pagination and sorting information
     * @return a page of {@link UserProfileResponseDto} matching the criteria
     */
    public Page<UserProfileResponseDto> searchUsers(String username, String firstName, String lastName,
                                                    Pageable pageable) {
        return userRepository.searchUsers(username, firstName, lastName, pageable).map(UserMapper::toUserProfileDto);
    }

    /**
     * Retrieves all users with pagination.
     *
     * @param pageable the pagination and sorting information
     * @return a page of {@link UserProfileResponseDto}
     */
    public Page<UserProfileResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserMapper::toUserProfileDto);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user
     * @return the {@link UserProfileResponseDto} of the user
     * @throws ResourceNotFoundException if the user is not found
     */
    public UserProfileResponseDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
        return UserMapper.toUserProfileDto(user);
    }

    /**
     * Updates user information by ID as an administrator.
     *
     * @param id the ID of the user to update
     * @param request the admin update request data
     * @return the updated {@link UserProfileResponseDto}
     * @throws ResourceNotFoundException if the user does not exist
     */
    public UserProfileResponseDto updateUserById(Long id, UserAdminUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));

        if (request.firstName() != null)
            user.setFirstName(request.firstName());
        if (request.lastName() != null)
            user.setLastName(request.lastName());
        if (request.role() != null)
            user.setRole(Role.valueOf(request.role().toString()));

        return UserMapper.toUserProfileDto(userRepository.save(user));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete
     * @return true if the deletion was successful
     * @throws ResourceNotFoundException if the user is not found
     */
    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            throw new ResourceNotFoundException("User with ID " + id + " not found.");
        }
    }
}
