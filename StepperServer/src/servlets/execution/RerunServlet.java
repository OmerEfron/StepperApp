package servlets.execution;

import StepperEngine.Stepper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
@WebServlet(name="rerunFlow", urlPatterns = "/execution/rerun")
public class RerunServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(ServletUtils.UUID_PARAMETER);
        if(uuid != null) {
            String newExecution = stepper.reRunFlow(uuid);
            if (newExecution != null) {
                ServletUtils.sendResponse(newExecution, newExecution.getClass(), resp);
            }
            else {
                ServletUtils.sendBadRequest(resp, "No such execution "+ uuid);
            }
        }
        else{
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(ServletUtils.UUID_PARAMETER));
        }
    }
}
