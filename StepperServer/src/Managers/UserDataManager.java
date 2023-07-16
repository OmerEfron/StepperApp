package Managers;

import DTO.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDataManager {
    private Map<String,Integer> userVersion=new HashMap<>();
    private Map<String, UserData> userData=new HashMap<>();

    public synchronized void addUser(String userName){
        userVersion.put(userName,0);
        userData.put(userName,new UserData(userName));
    }

    public synchronized Map<String, Integer> getUserVersion() {
        return userVersion;
    }

    public synchronized Map<String, UserData> getUserData() {
        return userData;
    }
}
