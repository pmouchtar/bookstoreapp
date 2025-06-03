package com.petros.bookstore.dto.UserDTO;

import com.petros.bookstore.model.enums.Role;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserAdminUpdateRequestDto(

        @Size(max = 50, message = "First name must be at most 50 characters") @Pattern(regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ]+$", message = "First name must contain only letters (no numbers or symbols)") String firstName,

        @Size(max = 50, message = "Last name must be at most 50 characters") @Pattern(regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ]+$", message = "Last name must contain only letters (no numbers or symbols)") String lastName,

        Role role

) {
}