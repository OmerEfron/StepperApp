package users.roles;

import java.util.Collection;
import java.util.List;

public interface RoleDefinition {
    String getName();
    String getDescription();
    Collection<String> getAllowedFlows();
    void addFlow(String flowName);
}
