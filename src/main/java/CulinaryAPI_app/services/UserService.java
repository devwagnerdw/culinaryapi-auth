package CulinaryAPI_app.services;

import CulinaryAPI_app.models.UserModel;

public interface UserService {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserModel saveUser(UserModel userModel);


}
