package com.careercompass.service.impl;

import com.careercompass.dto.UserDTO;
import com.careercompass.entity.Role;
import com.careercompass.entity.User;
import com.careercompass.entity.Category;
import com.careercompass.entity.Gender;
import com.careercompass.exception.DuplicateResourceException;
import com.careercompass.exception.ResourceNotFoundException;
import com.careercompass.repository.UserRepository;
import com.careercompass.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO register(UserDTO userDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException(
                    "User with email " + userDTO.getEmail() + " already exists");
        }

        User user = User.builder()
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .phone(userDTO.getPhone())
                .state(userDTO.getState())
                .category(null)
                .gender(parseOptionalGender(userDTO.getGender()))
                .role(Role.STUDENT)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());
        return mapToDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword()) &&
                user.get().getEnabled()) {
            return user;
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setState(userDTO.getState());
        user.setCategory(parseOptionalCategory(userDTO.getCategory()));
        user.setGender(parseOptionalGender(userDTO.getGender()));

        User updatedUser = userRepository.save(user);
        log.info("User profile updated: {}", userId);
        return mapToDTO(updatedUser);
    }

    @Override
    public UserDTO updateCategory(Long userId, String category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        user.setCategory(parseOptionalCategory(category));
        User updatedUser = userRepository.save(user);
        log.info("User category updated: {}", userId);
        return mapToDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByState(String state) {
        return userRepository.findByState(state).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        userRepository.delete(user);
        log.info("User deleted: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalStudents() {
        return (long) userRepository.findByRole(Role.STUDENT).size();
    }

    private Category parseOptionalCategory(String category) {
        return category == null || category.isBlank() ? null : Category.valueOf(category);
    }

    private Gender parseOptionalGender(String gender) {
        return gender == null || gender.isBlank() ? null : Gender.valueOf(gender);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .state(user.getState())
                .category(user.getCategory() != null ? user.getCategory().toString() : null)
                .gender(user.getGender() != null ? user.getGender().toString() : null)
                .role(user.getRole().toString())
                .enabled(user.getEnabled())
                .build();
    }
}
