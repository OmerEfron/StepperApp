import DTO.ExecutionsStatistics.FlowExecutionStats;
import DTO.FlowDetails.FlowDetails;
import DTO.FlowExecutionData.FlowExecutionData;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Stepper;
import users.roles.FlowDetailsFilter;
import users.roles.RoleDefinition;

import java.util.*;
import java.util.stream.Collectors;

public class StepperWithRoles extends Stepper {
    public List<FlowDetails> getFlowsDetails(RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        return getFlowsDetails().stream().filter(flowDetails -> combinedFlows.contains(flowDetails.getFlowName())).collect(Collectors.toList());
    }
    public List<String> getFlowNames(RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        return getFlowNames().stream().filter(combinedFlows::contains).collect(Collectors.toList());
    }

    public List<FlowExecutionData> getFlowExecutionDataList(RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        return getFlowExecutionDataList().stream().filter(flowExecutionData -> combinedFlows.contains(flowExecutionData.getFlowName())).collect(Collectors.toList());
    }
    public Map<String, List<FlowExecutionData>> getFlowExecutionDataMap(RoleDefinition ... roles) {
        Set<String> combinedFlows = getCombinedFlows(roles);
        Map<String, List<FlowExecutionData>> flowExecutionDataMap = getFlowExecutionDataMap();
        Map<String, List<FlowExecutionData>> filteredMap = new HashMap<>();
        for(String key: flowExecutionDataMap.keySet()){
            if(combinedFlows.contains(key)){
                filteredMap.put(key, flowExecutionDataMap.get(key));
            }
        }
        return filteredMap;
    }
    private static Set<String> getCombinedFlows(RoleDefinition[] roles) {
        Set<String> flowSet = new HashSet<>();
        Arrays.stream(roles).forEach(role->flowSet.addAll(role.getAllowedFlows()));
        return flowSet;
    }
}
