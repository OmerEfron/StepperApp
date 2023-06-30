package DTO.ExecutionsStatistics.api;

public interface StepExecutionStats {

    Integer getNumOfExecutions();

    Long getAvgTimeOfExecutions();
    String getStepName();
}
