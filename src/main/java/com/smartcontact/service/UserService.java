package com.smartcontact.service;

import com.smartcontact.entity.User;

public interface UserService {
    User registerUser(User user);
    User getUserByEmail(String email);
    User getUserById(Long id);
    User updateUser(User user);
    boolean changePassword(User user, String oldPassword, String newPassword);
}
