package Refresher.Users;

import DTO.UserData;

import java.util.List;
import java.util.Map;

public class UsersAndVersion {
    final private List<UserData> entries;
    final private Map<String, Integer> userVersion;

    public UsersAndVersion(List<UserData> entries, Map<String, Integer> userVersion) {
        this.entries = entries;
        this.userVersion = userVersion;
    }

    public List<UserData> getEntries() {
        return entries;
    }

    public Map<String, Integer> getUserVersion() {
        return userVersion;
    }
}
