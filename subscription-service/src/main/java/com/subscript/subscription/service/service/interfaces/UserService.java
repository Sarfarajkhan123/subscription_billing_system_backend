package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.User;
import com.subscript.subscription.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(Integer userId);
    User getUserByEmail(String email);
    User updateUser(Integer userId, User updatedData);
    void deleteUser(Integer userId);
    User deactivateUser(Integer userId);
    User activateUser(Integer userId);
}
