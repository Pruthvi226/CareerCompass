package com.careercompass.service;

import com.careercompass.dto.UserDTO;
import com.careercompass.entity.User;
import java.util.Optional;
import java.util.List;

/**
 * User service interface
 */
public interface UserService {

    UserDTO register(UserDTO userDTO);

    Optional<User> login(String email, String password);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    UserDTO updateProfile(Long userId, UserDTO userDTO);

    UserDTO updateCategory(Long userId, String category);

    List<UserDTO> getAllStudents();

    List<UserDTO> getUsersByState(String state);

    void deleteUser(Long userId);

    boolean userExists(String email);

    User getCurrentUser();

    Long getTotalStudents();
}
