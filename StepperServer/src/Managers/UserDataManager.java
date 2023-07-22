package Managers;

import DTO.UserData;
import users.roles.RoleDefinition;
import users.roles.RoleImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserDataManager {
    private Map<String,Integer> userVersion=new HashMap<>();
    private Map<String, UserData> userData=new HashMap<>();

    public synchronized void addUser(String userName){
        userVersion.put(userName,0);
        userData.put(userName,new UserData(userName));
    }

    public synchronized void addRoles(String userName, RoleImpl role){
        UserData userDataToUpdate = userData.get(userName);
        role.getAllowedFlows().stream().forEach(userDataToUpdate::addFlow);
        userDataToUpdate.addRole(role.getName());
        addVersion(userName);
    }
    public synchronized void removeRoles(String userName, RoleImpl role, Set<RoleDefinition> allowedRoles){
        UserData userDataToUpdate = userData.get(userName);
        role.getAllowedFlows().stream().forEach(userDataToUpdate::removeFlow);
        userDataToUpdate.removeRole(role.getName());
        allowedRoles.stream().forEach(roleDefinition -> roleDefinition.getAllowedFlows().stream().forEach(userDataToUpdate::addFlow));
        addVersion(userName);
    }

    public synchronized Map<String, Integer> getUserVersion() {
        return userVersion;
    }

    public synchronized Map<String, UserData> getUserData() {
        return userData;
    }
    public synchronized void setAsManager(String userName) {
        userData.get(userName).setManager(true);
    }
    public synchronized void enableManager(String userName) {
        userData.get(userName).setManager(false);
    }

    public synchronized void addExecutionToUser(String username) {
        userData.get(username).addExecution();
        addVersion(username);
    }
    public synchronized void addVersion(String userName){
        int i = userVersion.get(userName) + 1;
        userVersion.put(userName,i);
    }
}
