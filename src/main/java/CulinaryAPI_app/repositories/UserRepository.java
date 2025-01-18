package CulinaryAPI_app.repositories;

import CulinaryAPI_app.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel,UUID> {
}
