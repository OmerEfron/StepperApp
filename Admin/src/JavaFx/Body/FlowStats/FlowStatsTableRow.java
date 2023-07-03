package JavaFx.Body.FlowStats;



import DTO.ExecutionsStatistics.api.FlowExecutionStatsDefinition;
import DTO.ExecutionsStatistics.api.StepExecutionStats;

import java.util.ArrayList;
import java.util.List;

public class FlowStatsTableRow {

    private final String flowName;

    private final Integer numOfExecutions;
    private final Long avgTimeOfExecutions;

    private final List<StepExecutionStats> stepExecutionStatisticsList;

    public FlowStatsTableRow(FlowExecutionStatsDefinition flowExecutionStats) {
        this.flowName = flowExecutionStats.getFlowName();
        this.numOfExecutions = flowExecutionStats.getNumOfExecutions();
        this.avgTimeOfExecutions = flowExecutionStats.getAvgTimeOfExecutions();
        this.stepExecutionStatisticsList = flowExecutionStats.getStepExecutionsStats();
    }

    public String getFlowName() {
        return flowName;
    }

    public Integer getNumOfExecutions() {
        return numOfExecutions;
    }

    public Long getAvgTimeOfExecutions() {
        return avgTimeOfExecutions;
    }

    public List<StepExecutionStats> getStepExecutionStatisticsList() {
        return stepExecutionStatisticsList;
    }
}
