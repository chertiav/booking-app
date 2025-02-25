package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.exception.RegistrationException;
import com.chertiavdev.bookingapp.mapper.UserMapper;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.Role.RoleName;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.RoleRepository;
import com.chertiavdev.bookingapp.repository.UserRepository;
import com.chertiavdev.bookingapp.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto register(UserRegisterRequestDto requestDto) throws RegistrationException {
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("User with email: " + email + " already exists");
        }
        User user = userMapper.toModel(requestDto);
        user.setRoles(getSetOfUserRole());
        return userMapper.toDto(userRepository.save(user));
    }

    private Set<Role> getSetOfUserRole() {
        return Set.of(roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found role: "
                        + RoleName.USER.name())));
    }
}
