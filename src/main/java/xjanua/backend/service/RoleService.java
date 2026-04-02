package xjanua.backend.service;

import org.springframework.stereotype.Service;

import xjanua.backend.model.Role;
import xjanua.backend.repository.RoleRepo;
import xjanua.backend.util.constant.ResponseConstants;
import xjanua.backend.util.exception.ResourceNotFoundException;

@Service
public class RoleService {
    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Role findByName(String name) {
        return roleRepo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseConstants.ROLE_NOT_FOUND_MESSAGE));
    }
}
