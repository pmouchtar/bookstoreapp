package com.petros.bookstore.service;

import com.petros.bookstore.dto.UserProfileDto;
import com.petros.bookstore.mapper.UserMapper;
import com.petros.bookstore.model.User;
import com.petros.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.GONE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(GONE,
                        "The user account has been deleted or inactivated"));
    }

    public Page<UserProfileDto> searchUsers(String username, String firstName, String lastName, Pageable pageable) {
        return userRepository.searchUsers(username, firstName, lastName, pageable)
                .map(UserMapper::toUserProfileDto);
    }

    public Page<UserProfileDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toUserProfileDto);
    }
}
