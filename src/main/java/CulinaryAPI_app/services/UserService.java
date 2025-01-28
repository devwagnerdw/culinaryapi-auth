package CulinaryAPI_app.services;

import CulinaryAPI_app.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserModel save(UserModel userModel);

    Page<UserModel> findAll(Pageable pageable);

    Optional<UserModel> findById(UUID userId);

    void deleteUser(UserModel userModel);

    UserModel updateUser(UserModel userModel);

    void updatePassword(UserModel userModel);

    void updateImage(UserModel userModel);

    UserModel saveUser(UserModel userModel);

}
