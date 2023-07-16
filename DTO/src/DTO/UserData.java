package DTO;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    private String userName;
    private List<String> roles;
    private int numOfExecutions;
    private int numOfFlows;
    private boolean isManager;

    public UserData (String userName){
        this.userName=userName;
        roles=new ArrayList<>();
        numOfExecutions=0;
        numOfFlows=0;
        isManager=false;
    }

    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    public synchronized void setRoles(List<String> roles) {
        this.roles = roles;
    }
    public synchronized void addRole(String roleName){
        roles.add(roleName);
    }

    public synchronized void setNumOfExecutions(int numOfExecutions) {
        this.numOfExecutions = numOfExecutions;
    }
    public synchronized void addExecution(){
        numOfExecutions++;
    }


    public synchronized void setNumOfFlows(int numOfFlows) {
        this.numOfFlows = numOfFlows;
    }

    public synchronized void setManager(boolean manager) {
        isManager = manager;
    }

    public synchronized String getUserName() {
        return userName;
    }

    public synchronized List<String> getRoles() {
        return roles;
    }

    public synchronized int getNumOfExecutions() {
        return numOfExecutions;
    }

    public synchronized int getNumOfFlows() {
        return numOfFlows;
    }

    public synchronized boolean isManager() {
        return isManager;
    }
}
