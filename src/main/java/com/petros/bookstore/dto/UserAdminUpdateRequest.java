package com.petros.bookstore.dto;

import com.petros.bookstore.model.enums.Role;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminUpdateRequest {

    //@NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    @Pattern(
            regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ]+$",
            message = "First name must contain only letters (no numbers or symbols)"
    )
    private String firstName;

    //@NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    @Pattern(
            regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ]+$",
            message = "Last name must contain only letters (no numbers or symbols)"
    )
    private String lastName;


    private Role role;
}
