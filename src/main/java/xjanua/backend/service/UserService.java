package xjanua.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import xjanua.backend.model.User;
import xjanua.backend.repository.UserRepo;
import xjanua.backend.util.SecurityUtil;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User save(User user) {
        return userRepo.save(user);
    }

    public User fetchById(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.USER_NOT_FOUND_MESSAGE));
    }

    public User fetchMe() {
        return fetchById(SecurityUtil.getCurrentUserLogin());
    }

    public List<String> getCurrentUserAuthorities() {
        return SecurityUtil.getCurrentUserAuthorities();
    }
}