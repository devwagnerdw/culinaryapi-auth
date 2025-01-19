package CulinaryAPI_app.services.impl;


import CulinaryAPI_app.models.UserModel;
import CulinaryAPI_app.repositories.UserRepository;
import CulinaryAPI_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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


}
