package com.petros.bookstore.controller;

import com.petros.bookstore.dto.authdto.AuthenticationRequestDto;
import com.petros.bookstore.dto.authdto.AuthenticationResponseDto;
import com.petros.bookstore.dto.registrationdto.RegistrationRequestDto;
import com.petros.bookstore.dto.registrationdto.RegistrationResponseDto;
import com.petros.bookstore.mapper.UserRegistrationMapper;
import com.petros.bookstore.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication and registration.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    private final UserRegistrationMapper userRegistrationMapper;

    /**
     * Authenticates a user and returns authentication response with JWT token.
     *
     * @param authenticationRequestDto
     *            the login request DTO containing credentials
     * @return the authentication response DTO with JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody final AuthenticationRequestDto authenticationRequestDto) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDto));
    }

    /**
     * Registers a new user after validating registration data.
     *
     * @param registrationDto
     *            the registration request DTO
     * @return the registration response DTO with created user info
     */
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> registerUser(
            @Valid @RequestBody final RegistrationRequestDto registrationDto) {
        var registeredUser = authenticationService.registerUser(//
                userRegistrationMapper.toEntity(registrationDto));
        return ResponseEntity.ok(userRegistrationMapper.toRegistrationResponseDto(registeredUser));
    }
}
