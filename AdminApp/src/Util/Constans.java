package Util;

import DTO.ExecutionsStatistics.FlowExecutionStats;
import com.google.gson.Gson;

public class Constans {
    public final static String BASE_URL = "http://localhost:8080/StepperServer_Web_exploded";
    public final static String UPLOAD_FILE =BASE_URL+ "/upload-file";
    public final static String GET_FLOW_NAMES=BASE_URL+"/flows_names";
    public final static String FLOW_STATS=BASE_URL+"/stats";
    public final static String TEST=BASE_URL+"/test";
    public final static String FLOW_NAME_PARAMETER="flow_name";
    public final static FlowExecutionStats FLOW_EXECUTION_STATS= new FlowExecutionStats();


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
