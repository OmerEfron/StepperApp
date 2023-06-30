package DTO.ExecutionsStatistics.impl;


import DTO.ExecutionsStatistics.api.StepExecutionStats;

public class StepExecutionStatsImpl implements StepExecutionStats {

    private final String stepName;

    private final Integer numOfExecutions;
    private final Long avgTimeOfExecutions;

    public StepExecutionStatsImpl(String stepName, Integer numOfExecutions, Long avgTimeOfExecutions) {
        this.stepName = stepName;
        this.numOfExecutions = numOfExecutions;
        this.avgTimeOfExecutions = avgTimeOfExecutions;
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
    public String getStepName() {
        return stepName;
    }
}
