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
import com.chertiavdev.bookingapp.repository.RoleRepository;
import com.chertiavdev.bookingapp.repository.UserRepository;
import com.chertiavdev.bookingapp.service.UserService;
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
        log.info("Attempting to register a new user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: User with email {} already exists", email);
            throw new RegistrationException("User with email: " + email + " already exists");
        }

        User user = userMapper.toModel(requestDto);
        log.debug("Mapped UserRegisterRequestDto to User entity with email: {}", user.getEmail());

        try {
            user.setRoles(getSetOfUserRole(RoleName.USER));
            log.debug("Assigned roles to new user: {}", user.getRoles());
        } catch (EntityNotFoundException ex) {
            log.error("Role assignment failed: {}", ex.getMessage(), ex);
            throw ex;
        }

        User savedUser = userRepository.save(user);
        log.info("User registered successfully [email={}, id={}]", email, savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_USER_NOT_FOUND + "email:"
                        + email));
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

    @Override
    public UserWithRoleDto updateRoleByUsersId(Long id, UserUpdateRoleRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERROR_USER_NOT_FOUND + "id:" + id));
        user.setRoles(getSetOfUserRole(requestDto.getRoleName()));
        return userMapper.toUserWithRoleDto(userRepository.save(user));
    }

    private Set<Role> getSetOfUserRole(RoleName roleName) {
        return Set.of(roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found role: "
                        + roleName.name())));
    }
}
