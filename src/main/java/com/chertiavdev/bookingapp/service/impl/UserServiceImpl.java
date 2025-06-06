package com.chertiavdev.bookingapp.service.impl;

import com.chertiavdev.bookingapp.dto.user.UserDto;
import com.chertiavdev.bookingapp.dto.user.UserRegisterRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserUpdateRoleRequestDto;
import com.chertiavdev.bookingapp.dto.user.UserWithRoleDto;
import com.chertiavdev.bookingapp.exception.EntityNotFoundException;
import com.chertiavdev.bookingapp.exception.RegistrationException;
import com.chertiavdev.bookingapp.mapper.UserMapper;
import com.chertiavdev.bookingapp.model.Role;
import com.chertiavdev.bookingapp.model.Role.RoleName;
import com.chertiavdev.bookingapp.model.User;
import com.chertiavdev.bookingapp.repository.role.RoleRepository;
import com.chertiavdev.bookingapp.repository.user.UserRepository;
import com.chertiavdev.bookingapp.service.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    public static final String ERROR_USER_NOT_FOUND = "User not found with ";
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
        user.setRoles(getSetOfUserRole(RoleName.USER));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_USER_NOT_FOUND + "email:"
                        + email));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_USER_NOT_FOUND + "id:" + id));
    }

    @Transactional
    @Override
    public UserDto updateByEmail(String email, UserUpdateRequestDto requestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_USER_NOT_FOUND + "email:"
                        + email));
        userMapper.updateUserFromDto(requestDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserWithRoleDto updateRoleByUsersId(Long id, UserUpdateRoleRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_USER_NOT_FOUND + "id:" + id));
        user.setRoles(getSetOfUserRole(requestDto.getRoleName()));
        return userMapper.toUserWithRoleDto(userRepository.save(user));
    }

    private Set<Role> getSetOfUserRole(RoleName roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found role: "
                        + roleName.name()));
        return new HashSet<>(Set.of(role));
    }
}
