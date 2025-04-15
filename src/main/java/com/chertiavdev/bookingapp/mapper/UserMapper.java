package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.chertiavdev.bookingapp.mapper.password.EncodedMapping;
import com.chertiavdev.bookingapp.mapper.password.PasswordEncoderMapper;
import com.chertiavdev.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = {PasswordEncoderMapper.class, RoleMapper.class})
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    User toModel(UserRegisterRequestDto requestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateUserFromDto(UserUpdateRequestDto requestDto, @MappingTarget User user);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserWithRoleDto toUserWithRoleDto(User user);
}
