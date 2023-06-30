package DTO.ExecutionsStatistics.api;

import java.util.List;

public interface FlowExecutionStatsDefinition {

    Integer getNumOfExecutions();

    Long getAvgTimeOfExecutions();
    String getFlowName();

    List<StepExecutionStats> getStepExecutionsStats();

    StepExecutionStats getStepExecutionStats(String stepName);


}
