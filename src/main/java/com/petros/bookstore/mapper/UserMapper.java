package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.userdto.UserProfileResponseDto;
import com.petros.bookstore.model.User;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper {
    public static UserProfileResponseDto toUserProfileDto(final User user) {
        return new UserProfileResponseDto(user.getId(), user.getFirstName(), //
                user.getLastName(), user.getUsername(), user.getRole());
    }
}
