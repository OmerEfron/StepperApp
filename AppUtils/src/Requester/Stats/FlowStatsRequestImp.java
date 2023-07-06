package Requester.Stats;

import Utils.Constants;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class FlowStatsRequestImp implements FlowStatsRequest{
    private final String STATS_URL = Constants.BASE_URL + Constants.STATS_URL;
    @Override
    public Request getAllFlowRequest() {
        return new Request.Builder()
                .url(STATS_URL)
                .get()
                .build();
    }

    @Override
    public Request getFlowRequest(String flowName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(STATS_URL).newBuilder();
        urlBuilder.addQueryParameter("flow_name", flowName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();

    }
}
