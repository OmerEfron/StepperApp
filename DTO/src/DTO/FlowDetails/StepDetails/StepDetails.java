package DTO.FlowDetails.StepDetails;


import DTO.FlowDetails.StepDetails.FlowIODetails.Input;
import DTO.FlowDetails.StepDetails.FlowIODetails.Output;

import java.util.List;

public interface StepDetails {
    String getStepName();
    boolean isReadOnly();


    List<Input> getInputs();
    List<Output> getOutputs();
}
