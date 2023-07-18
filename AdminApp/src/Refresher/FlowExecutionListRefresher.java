package Refresher;

import DTO.FlowExecutionData.FlowExecutionData;
import JavaFx.AdminUtils;
import Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import Utils.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static JavaFx.AdminUtils.EXECUTION_REQUEST;

public class FlowExecutionListRefresher extends TimerTask {

    private final Consumer<List<FlowExecutionData>> consumer;

    public FlowExecutionListRefresher(Consumer<List<FlowExecutionData>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        Utils.runAsync(EXECUTION_REQUEST.executionDataList(),
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("error");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()) {
                            Gson gson = Constants.GSON_INSTANCE;
                            Type listType = new TypeToken<List<FlowExecutionData>>() {
                            }.getType();
                            List<FlowExecutionData> flowExecutionDataList = gson.fromJson(response.body().string(), listType);
                            Platform.runLater(() -> consumer.accept(flowExecutionDataList));
                        }
                    }
                },
                AdminUtils.HTTP_CLIENT);
    }
}
