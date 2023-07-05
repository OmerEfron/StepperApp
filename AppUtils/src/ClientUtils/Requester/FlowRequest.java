package ClientUtils.Requester;

import okhttp3.Request;

public interface FlowRequest {
    Request getAllFlowRequest();
    Request getFlow(String flowName);
}
