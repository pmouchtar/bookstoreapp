package com.petros.bookstore.service;

import static com.petros.bookstore.enums.Role.USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.petros.bookstore.dto.userdto.UserAdminUpdateRequestDto;
import com.petros.bookstore.dto.userdto.UserProfileResponseDto;
import com.petros.bookstore.dto.userdto.UserProfileUpdateRequestDto;
import com.petros.bookstore.exception.customException.ResourceGoneException;
import com.petros.bookstore.exception.customException.ResourceNotFoundException;
import com.petros.bookstore.model.User;
import com.petros.bookstore.enums.Role;
import com.petros.bookstore.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User(1L, "John", "Doe", "john_doe", "pass123", USER);
    }

    @Test
    void testGetUserByUsernameFound() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("john_doe");

        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getPassword()).isEqualTo("pass123");
        assertThat(result.getRole()).isEqualTo(USER);
    }

    @Test
    void testGetUserByUsernameGone() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("john_doe")).isInstanceOf(ResourceGoneException.class)
                .hasMessage("The user account has been deleted");
    }

    @Test
    void testUpdateUserProfile() {
        UserProfileUpdateRequestDto updateRequest = new UserProfileUpdateRequestDto("Jane", null, null, "newpass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserProfile(1L, updateRequest);

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getPassword()).isEqualTo("encoded_pass");
        assertThat(result.getRole()).isEqualTo(USER);
        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testUpdateUserProfileUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> userService.updateUserProfile(1L, new UserProfileUpdateRequestDto(null, null, null, null)))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("User with ID 1 not found.");
    }

    @Test
    void testSearchUsers() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.searchUsers(any(), any(), any(), any())).thenReturn(page);

        Page<UserProfileResponseDto> result = userService.searchUsers("john", null, null, PageRequest.of(0, 10));

        assertThat(result).hasSize(1);
        verify(userRepository).searchUsers(any(), any(), any(), any());
    }

    @Test
    void testFindAll() {
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.searchUsers(any(), any(), any(), any())).thenReturn(page);

        Page<UserProfileResponseDto> result = userService.searchUsers(null, null, null, PageRequest.of(0, 10));

        assertThat(result).hasSize(1);
        verify(userRepository).searchUsers(null, null, null, PageRequest.of(0, 10));
    }

    @Test
    void testFindUserByIdFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileResponseDto dto = userService.findUserById(1L);

        assertThat(dto.firstName()).isEqualTo("John");
        assertThat(dto.role()).isEqualTo(USER);
        assertThat(dto.username()).isEqualTo("john_doe");
        assertThat(dto.lastName()).isEqualTo("Doe");
    }

    @Test
    void testFindUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(1L)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with ID 1 not found.");
    }

    @Test
    void testUpdateUserById() {
        UserAdminUpdateRequestDto updateRequest = new UserAdminUpdateRequestDto("Updated", null, Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserProfileResponseDto result = userService.updateUserById(1L, updateRequest);

        assertThat(result.firstName()).isEqualTo("Updated");
        assertThat(result.role()).isEqualTo(Role.ADMIN);
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.username()).isEqualTo("john_doe");
    }

    @Test
    void testUpdateUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserById(1L, new UserAdminUpdateRequestDto(null, null, null)))
                .isInstanceOf(ResourceNotFoundException.class).hasMessage("User with ID 1 not found.");
    }

    @Test
    void testDeleteUserByIdSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUserById(1L);

        assertThat(result).isTrue();
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserByIdNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserById(1L)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with ID 1 not found.");
    }
}
