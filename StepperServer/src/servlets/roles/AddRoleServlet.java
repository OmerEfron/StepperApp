package servlets.roles;
import users.UserManager;
import users.roles.RolesManager;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.roles.RoleImpl;
import utils.ServletUtils;
import utils.StepperUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "addRole", urlPatterns = "/role/addRole")
public class AddRoleServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserValidator userValidator = new UserValidatorImpl(req);
        if(!userValidator.isAdmin()){
            ServletUtils.sendBadRequest(resp, "Only admin has privilege for this action");
        }
        else {
            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            Gson gson = new Gson();
            RoleImpl newRole = gson.fromJson(json, RoleImpl.class);
            if (newRole != null) {
                RolesManager rolesManager = StepperUtils.getRolesManger(getServletContext());
                rolesManager.addRole(newRole);
                addRoleToUser(newRole);
                ServletUtils.sendResponse(true, Boolean.class, resp);
            } else {
                ServletUtils.sendResponse(false, Boolean.class, resp);
            }
        }
    }

    private void addRoleToUser(RoleImpl newRole) {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        for(String name: newRole.getUsers()){
            userManager.addRoleToUser(name, newRole);
        }
    }
}