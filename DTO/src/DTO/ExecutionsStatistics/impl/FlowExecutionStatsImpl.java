package DTO.ExecutionsStatistics.impl;


import DTO.ExecutionsStatistics.api.FlowExecutionStatsDefinition;
import DTO.ExecutionsStatistics.api.StepExecutionStats;
import StepperEngine.Flow.api.FlowDefinition;
import StepperEngine.Flow.api.StepUsageDecleration;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Flow.execute.StepData.StepExecuteData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowExecutionStatsImpl implements FlowExecutionStatsDefinition {

    private final String flowName;

    private final Integer numOfExecutions;
    private final Long avgTimeOfExecutions;

    private final List<StepExecutionStats> stepExecutionStatisticsList = new ArrayList<>();

    private final Map<String, StepExecutionStats> stepExecutionStatsMap = new HashMap<>();

    public FlowExecutionStatsImpl(FlowDefinition flowDefinition, List<FlowExecution> flowExecutions) {
        this.flowName = flowDefinition.getName();
        this.numOfExecutions = flowExecutions != null ? flowExecutions.size() : 0;
        if(numOfExecutions!= 0)
            this.avgTimeOfExecutions = flowExecutions.stream()
                .mapToLong(flowExecution -> flowExecution.getTotalTime().toMillis())
                .sum() / numOfExecutions;
        else
            this.avgTimeOfExecutions = 0L;
        setStepExecutionsStats(flowDefinition, flowExecutions);
    }

    private void setStepExecutionsStats(FlowDefinition flowDefinition, List<FlowExecution> flowExecutions) {
        for (StepUsageDecleration step : flowDefinition.getSteps()){
            String stepName = step.getStepFinalName();
            int stepNumOfExecutions = 0;
            long stepTotalTimeOfExecutions = 0;
            if(flowExecutions != null) {
                for (FlowExecution flowExecution : flowExecutions) {
                    StepExecuteData stepExecuteData = flowExecution.getStepExecuteData(stepName);
                    if (stepExecuteData != null) {
                        stepNumOfExecutions++;
                        stepTotalTimeOfExecutions += stepExecuteData.getTotalTime().toMillis();
                    }
                }
            }
            long stepAvgTimeOfExecutions = stepNumOfExecutions > 0 ? stepTotalTimeOfExecutions/stepNumOfExecutions : 0;
            StepExecutionStatsImpl stepExecutionStats = new StepExecutionStatsImpl(stepName, numOfExecutions, stepAvgTimeOfExecutions);
            stepExecutionStatisticsList.add(stepExecutionStats);
            stepExecutionStatsMap.put(stepName, stepExecutionStats);
        }
    }



    @Override
    public Integer getNumOfExecutions() {
        return numOfExecutions;
    }

    @Override
    public Long getAvgTimeOfExecutions() {
        return avgTimeOfExecutions;
    }

    @Override
    public String getFlowName() {
        return flowName;
    }

    @Override
    public List<StepExecutionStats> getStepExecutionsStats() {
        return stepExecutionStatisticsList;
    }

    @Override
    public StepExecutionStats getStepExecutionStats(String stepName) {
        return stepExecutionStatsMap.get(stepName);
    }
}
