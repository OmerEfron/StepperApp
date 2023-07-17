package utils.Valitator;
import DTO.FlowExecutionData.FlowExecutionData;
import StepperEngine.Stepper;
import jakarta.servlet.http.HttpServletRequest;
import users.UserManager;
import users.roles.RoleDefinition;
import users.roles.RoleImpl;
import users.roles.RolesManager;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.StepperUtils;

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
        Set<RoleDefinition> userRoles = getUserRoles();
        return userRoles.stream()
                .anyMatch(role -> role.getAllowedFlows().contains(flowName)) || isAdmin();
    }

    private Set<RoleDefinition> getUserRoles() {
        String username = getUsername();
        UserManager userManager = getUserManager();
        return userManager.getUserRoles(username);
    }

    private UserManager getUserManager() {
        return ServletUtils.getUserManager(request.getServletContext());
    }

    private String getUsername() {
        return (String) request.getSession().getAttribute(USERNAME);
    }

    @Override
    public Boolean isAllFlowRole() {
        Set<RoleDefinition> userRoles = getUserRoles();
        return userRoles.stream().anyMatch(role -> role.getName().equals(RolesManager.ALL_FLOWS_ROLE)) || isAdmin();
    }

    @Override
    public Boolean isExecutionAllowed(String uuid) {
        Stepper stepper = StepperUtils.getStepper(request.getServletContext());
        FlowExecutionData flowExecutionData = stepper.getFlowExecutionData(uuid);
        String username = getUsername();
        return flowExecutionData.getUserExecuted().equals(username) || isAdmin();
    }
}
