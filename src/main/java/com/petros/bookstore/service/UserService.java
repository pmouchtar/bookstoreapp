package com.petros.bookstore.service;

import com.petros.bookstore.dto.UserAdminUpdateRequestDto;
import com.petros.bookstore.dto.UserProfileResponseDto;
import com.petros.bookstore.dto.UserProfileUpdateRequestDto;
import com.petros.bookstore.exception.ResourceGoneException;
import com.petros.bookstore.exception.ResourceNotFoundException;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.model.User;
import com.petros.bookstore.model.enums.Role;
import com.petros.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User getUserByUsername(final String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new ResourceGoneException("The user account has been deleted or inactivated"));
  }

  public User updateUserProfile(final Long userId, UserProfileUpdateRequestDto updateRequest) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("User with ID " + userId + " not found."));

    if (updateRequest.getFirstName() != null) {
      user.setFirstName(updateRequest.getFirstName());
    }
    if (updateRequest.getLastName() != null) {
      user.setLastName(updateRequest.getLastName());
    }
    if (updateRequest.getUsername() != null) {
      user.setUsername(updateRequest.getUsername());
    }
    if (updateRequest.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
    }

    return userRepository.save(user);
  }

  //    public void deleteUserProfile(final Long userId) {
  //        User user = userRepository.findById(userId)
  //                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + "
  // not found."));
  //
  //        userRepository.delete(user);
  //    }

  public Page<UserProfileResponseDto> searchUsers(
      String username, String firstName, String lastName, Pageable pageable) {
    return userRepository
        .searchUsers(username, firstName, lastName, pageable)
        .map(UserMapper::toUserProfileDto);
  }

  public Page<UserProfileResponseDto> findAll(Pageable pageable) {
    return userRepository.findAll(pageable).map(UserMapper::toUserProfileDto);
  }

  public UserProfileResponseDto findUserById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));
    return UserMapper.toUserProfileDto(user);
  }

  public UserProfileResponseDto updateUserById(Long id, UserAdminUpdateRequestDto request) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found."));

    if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
    if (request.getLastName() != null) user.setLastName(request.getLastName());
    if (request.getRole() != null) user.setRole(Role.valueOf(request.getRole().toString()));

    return UserMapper.toUserProfileDto(userRepository.save(user));
  }

  public boolean deleteUserById(Long id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    } else {
      throw new ResourceNotFoundException("User with ID " + id + " not found.");
    }
  }
}
