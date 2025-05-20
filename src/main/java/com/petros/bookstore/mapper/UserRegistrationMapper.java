package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.RegistrationRequestDto;
import com.petros.bookstore.dto.RegistrationResponseDto;
import com.petros.bookstore.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {

    public User toEntity(RegistrationRequestDto registrationRequestDto) {
        final var user = new User();

        user.setFirstName(registrationRequestDto.firstName());
        user.setLastName(registrationRequestDto.lastName());
        user.setUsername(registrationRequestDto.username());
        user.setPassword(registrationRequestDto.password());

        return user;
    }

    public RegistrationResponseDto toRegistrationResponseDto(
            final User user) {

        return new RegistrationResponseDto(
                user.getUsername(), user.getFirstName(), user.getLastName(), user.getRole());
    }

}
