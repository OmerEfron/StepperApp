package Requester.Users;

import Utils.Constants;
import okhttp3.Request;

public class UsersRequesterImpl implements UsersRequester {
    private final String GET_USERS_URL= Constants.BASE_URL+Constants.GET_USERS;
    @Override
    public Request getUsers() {
        return new Request.Builder()
                .url(GET_USERS_URL)
                .get()
                .build();
    }
}
