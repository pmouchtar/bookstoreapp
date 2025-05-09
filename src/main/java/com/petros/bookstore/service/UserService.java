package com.petros.bookstore.service;

import com.petros.bookstore.dto.UserProfileResponseDto;
import com.petros.bookstore.dto.UserProfileUpdateRequest;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.model.User;
import com.petros.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.GONE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(GONE,
                        "The user account has been deleted or inactivated"));
    }

    public User updateUserProfile(final String username, UserProfileUpdateRequest updateRequest) {
        User user = getUserByUsername(username);

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

    public void deleteUserByUsername(final String username) {
        User user = getUserByUsername(username);

        userRepository.delete(user);
    }

    public Page<UserProfileResponseDto> searchUsers(String username, String firstName, String lastName, Pageable pageable) {
        return userRepository.searchUsers(username, firstName, lastName, pageable)
                .map(UserMapper::toUserProfileDto);
    }

    public Page<UserProfileResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toUserProfileDto);
    }
}
