package com.chertiavdev.bookingapp.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Extended DTO representing a user's data with role information")
public class UserWithRoleDto extends UserDto {
    @Schema(description = "Roles of the user in the system", example = "[\"ADMIN\"]")
    private Set<String> roles;
}
