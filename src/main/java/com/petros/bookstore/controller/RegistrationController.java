package com.petros.bookstore.controller;

import com.petros.bookstore.dto.RegistrationRequestDto;
import com.petros.bookstore.dto.RegistrationResponseDto;
import com.petros.bookstore.mapper.UserRegistrationMapper;
import com.petros.bookstore.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistrationService userRegistrationService;

    private final UserRegistrationMapper userRegistrationMapper;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> registerUser(
            @Valid @RequestBody final RegistrationRequestDto registrationDTO) {

        final var registeredUser = userRegistrationService
                .registerUser(userRegistrationMapper.toEntity(registrationDTO));

        return ResponseEntity.ok(
                userRegistrationMapper.toRegistrationResponseDto(registeredUser)
        );
    }

}