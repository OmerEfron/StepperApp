package Requester.Users;

import okhttp3.Request;

import java.util.Map;

public interface UsersRequester {
    Request getUsers();
    Request getUsersData(Map<String, Integer> version);
}
