package Requester.Roles;

import okhttp3.Request;
import users.roles.RoleImpl;

public interface RoleRequest {
    Request getAllRoles();
    Request getRole(String roleName);

    Request addRole(RoleImpl role);
}
