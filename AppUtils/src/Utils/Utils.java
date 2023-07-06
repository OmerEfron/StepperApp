package Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class Utils {

    public static void runAsync(Request request, Callback callback, OkHttpClient httpClient) {

        Call call = httpClient.newCall(request);

        call.enqueue(callback);
    }

    public static void runSync(Request request, OkHttpClient httpClient){
        try {
            Response response = httpClient.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static  <T> T runSync(Request request, Class<T> returnType, OkHttpClient httpClient){
        try {
            Response response = httpClient.newCall(request).execute();
            if(response.isSuccessful()){
                Object returnObject = Constants.GSON_INSTANCE.fromJson(response.body().string(), returnType);
                return returnType.cast(returnObject);
            }else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static  String runSyncFile(Request request, OkHttpClient httpClient){
        try {
            Response response = httpClient.newCall(request).execute();
            if(!response.isSuccessful()){
                String responseBody = response.body().string();
                JsonElement jsonElement = JsonParser.parseString(responseBody);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonObject.get("message").getAsString();
            }else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
