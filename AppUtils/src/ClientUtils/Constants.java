package ClientUtils;

import com.google.gson.Gson;

public class Constants {

    public static final Gson GSON_INSTANCE = new Gson();
    public static final String port = ":8080";
    public final static String BASE_DOMAIN = "localhost";
    public static final String CONTEXT_PATH = "/StepperServer_Web_exploded";
    public final static String BASE_URL = "http://" + BASE_DOMAIN + port + CONTEXT_PATH;
    public static final String FLOW_URL = "/flows";
    public static final String EXECUTE_FLOW_URL = "/execution";
    public static final String EXECUTION_DATA_URL = "/execution/data";
    public static final String READY_URL = "/execution/ready";
    public static final String EXECUTION_STATUS_URL = "/execution/status";
    public static final String STATS_URL = "/stats";


}
