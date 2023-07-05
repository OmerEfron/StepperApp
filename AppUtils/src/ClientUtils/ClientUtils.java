package ClientUtils;

import okhttp3.*;

import java.io.IOException;

public class ClientUtils {

    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();

    public static void runAsync(Request request, Callback callback) {

        Call call = HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void runSync(Request request){
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T runSync(Request request, Class<T> returnType){
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
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



}
