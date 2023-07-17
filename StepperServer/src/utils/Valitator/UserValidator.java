package utils.Valitator;

public interface UserValidator {
    Boolean isAdmin();
    Boolean isLoggedIn();
    Boolean isFlowAllowed(String flowName);
    Boolean isAllFlowRole();
    Boolean isExecutionAllowed(String uuid);
}
