package servlets.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;
import utils.Valitator.UserValidator;
import utils.Valitator.UserValidatorImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserValidator userValidator = new UserValidatorImpl(req);
        if(userValidator.isLoggedIn()){
            if(userValidator.isAdmin()){
                ServletUtils.setAdminLoggedIn(false);
            }else{
                UserManager userManager = ServletUtils.getUserManager(getServletContext());
                String username = SessionUtils.getUsername(req);
                userManager.removeUser(username);
            }
        }else{
            ServletUtils.sendNotLoggedInBadRequest(resp);
        }
    }
}
