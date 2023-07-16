package users.roles;

import DTO.FlowDetails.FlowDetails;
import StepperEngine.Stepper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RolesManager {
    private final Map<String, RoleImpl> roleMap=new HashMap<>();
    public static final String READ_ONLY_FLOWS = "Read Only Flows";
    public static final String ALL_FLOWS_ROLE = "All flows";

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
        roleMap.put(READ_ONLY_FLOWS,new RoleImpl(READ_ONLY_FLOWS,"Just read only flow's belong to this role", flows));

    }

    public void addAllFlowsRole(Stepper stepper) {
        roleMap.put(ALL_FLOWS_ROLE,new RoleImpl("All Flows","The role contains all flows in system", stepper.getFlowNames()));
    }
    public synchronized RoleImpl getDefaultRole(){
        return roleMap.get(READ_ONLY_FLOWS);
    }

    public Map<String, RoleImpl> getRoleMap() {
        return roleMap;
    }
    public synchronized void addRole(RoleImpl role){
        roleMap.put(role.getName(),role);
    }
    public synchronized void addUserToRole(String roleName,String userName){
        roleMap.get(roleName).addUser(userName);
    }
}
