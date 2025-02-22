package CulinaryAPI_app.services.impl;

import CulinaryAPI_app.enums.RoleType;
import CulinaryAPI_app.models.RoleModel;
import CulinaryAPI_app.repositories.RoleRepository;
import CulinaryAPI_app.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

     private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Override
    public Optional<RoleModel> findByRoleName(RoleType roleType) {
     return    roleRepository.findByRoleName(roleType);
    }
}
