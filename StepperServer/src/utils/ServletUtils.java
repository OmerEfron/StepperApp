package utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exceptions.MissingParamException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServletUtils {

    public final static String FLOW_NAME_PARAMETER = "flow_name";


    public static void sendBadRequest(HttpServletResponse response, String message) throws IOException {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("message", message);
// Set the response status code and content type
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
// Write the JSON object as the response
        Gson gson = new Gson();
        String json = gson.toJson(errorJson);
        response.getWriter().write(json);
    }

    public static void sendResponse(Object obj, Class<?> expectedClass, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(obj, expectedClass);
        response.getWriter().write(jsonResponse);
    }

    public static String getMissingParameterMessage(String param_name){
        return String.format("missing parameter: %s", param_name);
    }

    public static Map<String, String> getParamMap(HttpServletRequest request, String ... params) throws MissingParamException {
        Map<String,String> result = new HashMap<>();
        for(String param:params){
            String currParamData = request.getParameter(param);
            if(currParamData == null){
                throw new MissingParamException(param);
            }
            result.put(param, currParamData);
        }
        return result;
    }
}