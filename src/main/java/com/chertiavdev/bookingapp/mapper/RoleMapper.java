package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.model.Role;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    @Named("rolesToStrings")
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());
    }
}
