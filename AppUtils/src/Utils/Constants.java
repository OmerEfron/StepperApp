package Utils;

import DTO.DataAndType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final Gson GSON_INSTANCE;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DataAndType.class, new DataAndType.DataAndTypeAdapter());
        GSON_INSTANCE = gsonBuilder.create();
    }
    public static final String port = ":8080";
    public final static String BASE_DOMAIN = "localhost";
    public static final String CONTEXT_PATH = "/StepperServer_Web_exploded";
    public final static String BASE_URL = "http://" + BASE_DOMAIN + port + CONTEXT_PATH;
    public static final String FLOW_URL = "/flows";
    public static final String EXECUTE_FLOW_URL = "/execution";
    public static final String EXECUTION_DATA_URL = "/execution/data";
    public static final String READY_URL = "/execution/ready";
    public static final String EXECUTION_STATUS_URL = "/execution/status";
    public static final String EXECUTION_RERUN_URL = "/execution/rerun";
    public static final String CONTINUATION_URL = "/execution/continue";
    public static final String STATS_URL = "/stats";
    public static final String STATS_VERSION_URL = "/stats_version";
    public static final String UPLOAD_FILE_URL = "/upload-file";
    public static final String FLOWS_NAMES ="/flows_names";
    public static final List<String> STRING_LIST_INSTANCE = new ArrayList<>();

}
