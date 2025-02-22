package CulinaryAPI_app.repositories;

import CulinaryAPI_app.enums.RoleType;
import CulinaryAPI_app.models.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleModel,UUID> {

    Optional<RoleModel> findByRoleName(RoleType name);
}
