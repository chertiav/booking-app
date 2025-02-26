package com.chertiavdev.bookingapp.mapper;

import com.chertiavdev.bookingapp.config.MapperConfig;
import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.mapper.password.EncodedMapping;
import com.chertiavdev.bookingapp.mapper.password.PasswordEncoderMapper;
import com.chertiavdev.bookingapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {PasswordEncoderMapper.class})
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    User toModel(UserRegisterRequestDto requestDto);
}
