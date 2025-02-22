package CulinaryAPI_app.services;

import CulinaryAPI_app.enums.RoleType;
import CulinaryAPI_app.models.RoleModel;

import java.util.Optional;

public interface RoleService {
    Optional<RoleModel> findByRoleName(RoleType roleType);
}
