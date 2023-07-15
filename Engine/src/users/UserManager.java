package users;

import users.roles.RoleImpl;

import java.util.*;

public class UserManager {

    private final Set<String> usersSet;
    private final Map<String, Set<RoleImpl>> userRolesMap;

    public UserManager() {
        usersSet = new HashSet<>();
        userRolesMap=new HashMap<>();
    }
    public synchronized void addRoleToUser(String userName,RoleImpl role){
        userRolesMap.get(userName).add(role);
    }

    public synchronized void addUser(String username) {
        usersSet.add(username);
        userRolesMap.put(username,new HashSet<>());
    }

    public synchronized void removeUser(String username) {
        usersSet.remove(username);
        userRolesMap.remove(username);
    }

    public synchronized Set<String> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public synchronized Set<RoleImpl> getUserRoles(String userName) {
        return userRolesMap.get(userName);
    }

    public boolean isUserExists(String username) {
        return usersSet.contains(username);
    }

}
