package JavaFx;

import okhttp3.OkHttpClient;

public class AdminUtils {
    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();
}
