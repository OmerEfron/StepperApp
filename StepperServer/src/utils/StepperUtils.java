package utils;
import users.roles.RoleDefinition;
import users.roles.RolesManager;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import StepperEngine.StepperWithRolesAndUsers;
import jakarta.servlet.ServletContext;
import users.roles.RoleImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import users.UserManager;

public class StepperUtils {

    private static final String STEPPER_ATTRIBUTE_NAME = "stepper";
    private static final String STEPPER_LOADED_ATTRIBUTE = "is_stepper_loaded";
    private static final String ROLE_ATTRIBUTE="roles";
    private static final String ROLE_MAP_ATTRIBUTE = "role_map";
    private static final Object stepperLock = new Object();

    public static StepperWithRolesAndUsers getStepper(ServletContext servletContext){
        Stepper stepper;
        synchronized (stepperLock){
            if(servletContext.getAttribute(STEPPER_ATTRIBUTE_NAME) == null){
                stepper = new StepperWithRolesAndUsers();
//                try {
//                    stepper.load("C:\\Users\\Gil\\Desktop\\StepperApp2\\ex2.xml");
//                } catch (ReaderException | FlowBuildException e) {
//                    throw new RuntimeException(e);
//                }
                servletContext.setAttribute(STEPPER_ATTRIBUTE_NAME,stepper);
            }
        }
        return (StepperWithRolesAndUsers) servletContext.getAttribute(STEPPER_ATTRIBUTE_NAME);
    }
    public static boolean isStepperIn(ServletContext servletContext) {
        synchronized (stepperLock)
        {
            if (servletContext.getAttribute(STEPPER_LOADED_ATTRIBUTE)==null){
                servletContext.setAttribute(STEPPER_LOADED_ATTRIBUTE,false);
            }
        }
        return (Boolean) servletContext.getAttribute(STEPPER_LOADED_ATTRIBUTE);
    }
    public static void setStepperIn(ServletContext servletContext) {
        synchronized (stepperLock)
        {
            servletContext.setAttribute(STEPPER_LOADED_ATTRIBUTE,true);
        }
    }

    public static RolesManager getRolesManger(ServletContext servletContext){
        RolesManager rolesManager;
        synchronized (stepperLock){
            if(servletContext.getAttribute(ROLE_ATTRIBUTE)==null){
                rolesManager=new RolesManager(getStepper(servletContext));
                servletContext.setAttribute(ROLE_ATTRIBUTE,rolesManager);
            }
        }
        rolesManager=(RolesManager) servletContext.getAttribute(ROLE_ATTRIBUTE);
        return rolesManager;
    }

    public static Set<RoleDefinition> getUserRoles(ServletContext servletContext, String username){
        RolesManager rolesManger = getRolesManger(servletContext);
        return rolesManger.getUserRoles(username);
    }

    public static RoleDefinition[] getUserRolesAsArray(ServletContext servletContext, String username){
        Set<RoleDefinition> userRoles = getUserRoles(servletContext, username);
        return userRoles.toArray(new RoleDefinition[0]);
    }

    public static void addDefaultRoles(ServletContext servletContext){
        RolesManager rolesManager=(RolesManager) servletContext.getAttribute(ROLE_ATTRIBUTE);
        rolesManager.addReadOnlyRole(getStepper(servletContext));
        rolesManager.addAllFlowsRole(getStepper(servletContext));
    }
}
