package StepperEngine.Flow.execute;

import StepperEngine.Flow.api.FlowDefinition;

public class FlowExecutionWithUser extends FlowExecution{

    private final String userExecuting;

    public FlowExecutionWithUser(FlowDefinition flowDefinition, String username) {
        super(flowDefinition);
        this.userExecuting = username;
    }

    public String getUserExecuting() {
        return userExecuting;
    }
}
