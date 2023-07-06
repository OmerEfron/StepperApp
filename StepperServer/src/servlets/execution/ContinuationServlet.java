package servlets.execution;

import StepperEngine.Stepper;
import exceptions.MissingParamException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
import java.util.Map;
@WebServlet(name = "continuation", urlPatterns = "/execution/continue")
public class ContinuationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        try {
            Map<String, String> paramMap = ServletUtils.getParamMap(req, ServletUtils.UUID_PARAMETER, ServletUtils.FLOW_NAME_PARAMETER);
            String uuid = paramMap.get(ServletUtils.UUID_PARAMETER);
            String flowToContinue = paramMap.get(ServletUtils.FLOW_NAME_PARAMETER);
            String newExecution = stepper.applyContinuation(uuid, flowToContinue);
            if (newExecution != null) {
                ServletUtils.sendResponse(newExecution, newExecution.getClass(), resp);
            } else {
                ServletUtils.sendBadRequest(resp, "No such execution " + uuid);
            }
        }
        catch (MissingParamException e){
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(e.getMissingParamName()));
        }
    }

}
