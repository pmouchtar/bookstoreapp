package com.petros.bookstore.controller;

import com.petros.bookstore.dto.AuthenticationRequestDto;
import com.petros.bookstore.dto.AuthenticationResponseDto;
import com.petros.bookstore.dto.RegistrationRequestDto;
import com.petros.bookstore.dto.RegistrationResponseDto;
import com.petros.bookstore.mapper.UserRegistrationMapper;
import com.petros.bookstore.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    private final UserRegistrationMapper userRegistrationMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody final AuthenticationRequestDto authenticationRequestDto
    ) {
        return ResponseEntity.ok(
                authenticationService.authenticate(authenticationRequestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> registerUser(
            @Valid @RequestBody final RegistrationRequestDto registrationDTO) {

        final var registeredUser = authenticationService
                .registerUser(userRegistrationMapper.toEntity(registrationDTO));

        return ResponseEntity.ok(
                userRegistrationMapper.toRegistrationResponseDto(registeredUser)
        );
    }
}