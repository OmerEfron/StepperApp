package ClientUtils.Requester.flow;

import okhttp3.Request;

public interface FlowRequest {
    Request getAllFlowRequest();
    Request getFlow(String flowName);
}
