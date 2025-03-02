package com.chertiavdev.bookingapp.dto.user;

import com.chertiavdev.bookingapp.model.Role.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request for updating the user's role")
public class UserUpdateRoleRequestDto {
    @NotNull(message = "Role must not be null")
    @Schema(description = "Role to be updated",
            example = "ADMIN")
    private RoleName roleName;
}
