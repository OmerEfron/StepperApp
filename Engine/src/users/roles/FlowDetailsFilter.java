package users.roles;

import DTO.FlowDetails.FlowDetails;

import java.util.function.Predicate;

public class FlowDetailsFilter implements Predicate<FlowDetails> {

    private final RoleDefinition roleDefinition;

    public FlowDetailsFilter(RoleDefinition roleDefinition) {
        this.roleDefinition = roleDefinition;
    }

    @Override
    public boolean test(FlowDetails flowDetails) {
        return roleDefinition.getAllowedFlows().contains(flowDetails.getFlowName());
    }

}
