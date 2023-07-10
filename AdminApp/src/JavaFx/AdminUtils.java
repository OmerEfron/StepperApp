package JavaFx;

import Requester.Stats.FlowStatsRequestImp;
import Requester.execution.ExecutionRequestImpl;
import Requester.fileupload.FileUploadImpl;
import Requester.flow.flowNames.FlowsNamesRequestImpl;
import okhttp3.OkHttpClient;

public class AdminUtils {
    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();
    public final static FileUploadImpl FILE_UPLOAD=new FileUploadImpl();
    public final static FlowsNamesRequestImpl FLOWS_NAMES_REQUEST=new FlowsNamesRequestImpl();
    public final static FlowStatsRequestImp FLOW_STATS_REQUEST=new FlowStatsRequestImp();
    public final static ExecutionRequestImpl EXECUTION_REQUEST=new ExecutionRequestImpl();
}
