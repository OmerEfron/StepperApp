package servlets.roles;

import Managers.RolesManager;
import StepperEngine.Stepper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.roles.RoleImpl;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "getRoles", urlPatterns = "/role/getRoles")
public class GetRolesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, RoleImpl> roleMap= StepperUtils.getRolesMap(getServletContext());
        String roleName = req.getParameter(ServletUtils.ROLE_NAME_PARAMETER);
        if (roleName == null){
            List<RoleImpl> roles=new ArrayList<>(roleMap.values());
            ServletUtils.sendResponse(roles,roles.getClass(),resp);
        }else {
            RoleImpl role=roleMap.get(roleName);
            ServletUtils.sendResponse(role,role.getClass(),resp);
        }
    }
}
