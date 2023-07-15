package users.roles;

import DTO.FlowDetails.FlowDetails;
import StepperEngine.Stepper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RolesManager {
    private final Map<String, RoleImpl> roleMap=new HashMap<>();
    public RolesManager(Stepper stepper){
        addReadOnlyRole(stepper);
        addAllFlowsRole(stepper);
        roleMap.put("Test Role",new RoleImpl("Test Role","Test Role", stepper.getFlowNames().subList(0,1)));

    }

    public void addReadOnlyRole(Stepper stepper) {
        List<String> flows=new ArrayList<>();
        for(FlowDetails flow:stepper.getFlowsDetails())
        {
            if(flow.isFlowReadOnly())
                flows.add(flow.getFlowName());

        }
        roleMap.put("Read Only Flows",new RoleImpl("Read Only Flows","Just read only flow's belong to this role", flows));

    }

    public void addAllFlowsRole(Stepper stepper) {
        roleMap.put("All flows",new RoleImpl("All Flows","The role contains all flows in system", stepper.getFlowNames()));
    }

    public Map<String, RoleImpl> getRoleMap() {
        return roleMap;
    }
    public synchronized void addRole(RoleImpl role){
        roleMap.put(role.getName(),role);
    }
}
