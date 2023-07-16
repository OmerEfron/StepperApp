package utils.Valitator;
import jakarta.servlet.http.HttpServletRequest;
import users.UserManager;
import users.roles.RoleDefinition;
import users.roles.RoleImpl;
import utils.ServletUtils;
import utils.SessionUtils;

import java.util.Set;

import static utils.ServletUtils.ADMIN_USERNAME;
import static utils.SessionUtils.USERNAME;

public class UserValidatorImpl implements UserValidator{

    private final HttpServletRequest request;

    public UserValidatorImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Boolean isAdmin() {
        return SessionUtils.getUsername(request).equals(ADMIN_USERNAME);
    }

    @Override
    public Boolean isLoggedIn() {
        return SessionUtils.getUsername(request) != null;
    }

    @Override
    public Boolean isFlowAllowed(String flowName) {
        if(!isLoggedIn()){
            return false;
        } else if (isAdmin()) {
            return true;
        }
        String username = getUsername();
        UserManager userManager = getUserManager();
        Set<RoleDefinition> userRoles = userManager.getUserRoles(username);
        return userRoles.stream()
                .anyMatch(role -> role.getAllowedFlows().contains(flowName));
    }

    private UserManager getUserManager() {
        return ServletUtils.getUserManager(request.getServletContext());
    }

    private String getUsername() {
        return (String) request.getSession().getAttribute(USERNAME);
    }

}
