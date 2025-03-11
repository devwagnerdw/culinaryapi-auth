package CulinaryAPI_app.services;

import CulinaryAPI_app.dtos.UserDto;
import CulinaryAPI_app.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {

    Page<UserModel> findAll(Pageable pageable);

    ResponseEntity<Object> getOneUser(UUID userId);

    ResponseEntity<Object> deactivateUser(UUID userId);

    ResponseEntity<Object> updateUser(UUID userId, UserDto userDto);

    ResponseEntity<Object> updatePassword(UUID userId, UserDto userDto);

    ResponseEntity<Object> updateImage(UUID userId, UserDto userDto);

    ResponseEntity<Object> registerUser(UserDto userDto);

    ResponseEntity<Object> registerAdmin(UserDto userDto);

    ResponseEntity<Object> registerDelivery(UserDto userDto);
}