package CulinaryAPI_app.services.impl;


import CulinaryAPI_app.models.UserModel;
import CulinaryAPI_app.repositories.UserRepository;
import CulinaryAPI_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;


    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    @Override
    public UserModel saveUser(UserModel userModel) {
        return userRepository.save(userModel);

    }

    @Override
    public Page<UserModel> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        return userRepository.findById(userId);
    }


    @Override
    public void deleteUser(UserModel userModel) {
        userRepository.delete(userModel);
    }

    @Transactional
    @Override
    public UserModel updateUser(UserModel userModel) {
        return userRepository.save(userModel);
    }

    @Override
    public void updatePassword(UserModel userModel) {
        userRepository.save(userModel);
    }

    @Override
    public void updateImage(UserModel userModel) {
        userRepository.save(userModel);
    }


}
