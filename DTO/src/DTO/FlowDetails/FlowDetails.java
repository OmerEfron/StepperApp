package DTO.FlowDetails;



import DTO.FlowDetails.StepDetails.FlowIODetails.Input;
import DTO.FlowDetails.StepDetails.FlowIODetails.Output;
import DTO.FlowDetails.StepDetails.StepDetails;

import java.util.List;

public interface FlowDetails {

    String getFlowName();

    String getFlowDescription();

    List<String> getFormalOutputs();

    boolean isFlowReadOnly();
    String isFlowReadOnlyString();

    List<StepDetails> getSteps();
    List<String> getContinuationNames();
    List<Input> getFreeInputs();
    int getContinuationNumber();
    List<Output> getOutputs();
    List<String> getStepsNames();
}
