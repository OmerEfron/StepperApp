package users.roles;

import java.util.*;

public class RoleImpl implements RoleDefinition{
    private final String name;
    private final String description;
    private final Set<String> flows;

    public RoleImpl(String name, String description, String ... flows) {
        this.name = name;
        this.description = description;
        this.flows = new HashSet<>(Arrays.asList(flows));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Collection<String> getAllowedFlows() {
        return flows;
    }

    @Override
    public void addFlow(String flowName) {
        flows.add(flowName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleImpl role = (RoleImpl) o;
        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
