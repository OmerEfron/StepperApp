package servlets.roles;

import Managers.RolesManager;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.roles.RoleImpl;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "addRole", urlPatterns = "/role/addRole")
public class AddRoleServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();

        Gson gson = new Gson();
        RoleImpl newRole = gson.fromJson(json, RoleImpl.class);
        if(newRole!=null) {
            RolesManager rolesManager = StepperUtils.getRolesManger(getServletContext());
            rolesManager.addRole(newRole);
            ServletUtils.sendResponse(true, Boolean.class, resp);
        }else {
            ServletUtils.sendResponse(false, Boolean.class, resp);
        }
    }
}
