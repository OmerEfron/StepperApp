package Requester.Roles;

import Utils.Constants;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import users.roles.RoleImpl;

public class RoleRequestImpl implements RoleRequest{
    private final String GET_ROLE_URL= Constants.BASE_URL+Constants.GET_ROLE;
    private final String PUT_ROLE_URL= Constants.BASE_URL+Constants.PUT_ROLE;
    @Override
    public Request getAllRoles() {
        return new Request.Builder()
                .url(GET_ROLE_URL)
                .get()
                .build();
    }

    @Override
    public Request getRole(String roleName) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GET_ROLE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.ROLE_NAME_PARAMETER, roleName);
        String url = urlBuilder.build().toString();
        return new Request.Builder()
                .url(url)
                .build();
    }

    @Override
    public Request addRole(RoleImpl role) {
        String json = Constants.GSON_INSTANCE.toJson(role);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
        return new Request.Builder()
                .url(PUT_ROLE_URL)
                .put(requestBody)
                .build();
    }
}