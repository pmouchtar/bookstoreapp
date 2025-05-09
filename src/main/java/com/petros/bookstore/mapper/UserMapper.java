package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.UserProfileResponseDto;
import com.petros.bookstore.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserProfileResponseDto toUserProfileDto(final User user) {
        return new UserProfileResponseDto(user.getFirstName(), user.getLastName(), user.getUsername());
    }
}
