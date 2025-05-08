package com.petros.bookstore.mapper;

import com.petros.bookstore.dto.UserProfileDto;
import com.petros.bookstore.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public static UserProfileDto toUserProfileDto(final User user) {
        return new UserProfileDto(user.getFirstName(), user.getLastName(), user.getUsername());
    }
}
