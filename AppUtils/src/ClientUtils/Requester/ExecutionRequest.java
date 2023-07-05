package ClientUtils.Requester;

import okhttp3.Request;

public interface ExecutionRequest {
    Request isExecutionReadyRequest(String uuid);
    Request executeRequest(String uuid);
    Request createExecuteRequest(String flowName);
    Request addFreeInputRequest(String uuid, String dataName, String data);
    Request executionStatusRequest(String uuid);
    Request flowExecutionsRequest(String flowName);
    Request executionRequest(String uuid);
}