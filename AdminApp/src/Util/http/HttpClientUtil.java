package Util.http;

import Util.Constans;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class HttpClientUtil {
    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    public final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();
    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }
    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }
    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }
    public static void runSync(String finalUrl, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        try {
            Response response = call.execute();
            callback.onResponse(call, response);
        } catch (IOException e) {
            callback.onFailure(call, e);
        }
    }
    public static void runSyncFileUpload(File file, Callback callback) throws IOException {
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("application/xml")))
                .addFormDataPart("file_path", file.getPath())
                .build();
        Request request = new Request.Builder()
                .url(Constans.UPLOAD_FILE)
                .post(body)
                .build();
        Call call = HTTP_CLIENT.newCall(request);
        try {
            Response response = call.execute();
            callback.onResponse(call, response);
        } catch (IOException e) {
            callback.onFailure(call, e);
        }
    }


    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
