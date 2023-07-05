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
@WebServlet(urlPatterns = "/execution/ready")
public class ExecutionReadyToExecutedServlet extends HttpServlet {

    public static final String UUID_PARAMETER = "uuid";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());

        String uuid = req.getParameter(UUID_PARAMETER);
        if(uuid != null){
            Boolean executionReadyToBeExecuteStatus = stepper.getExecutionReadyToBeExecuteStatus(uuid);
            ServletUtils.sendResponse(executionReadyToBeExecuteStatus, executionReadyToBeExecuteStatus.getClass(), resp);
        }
        else{
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(UUID_PARAMETER));
        }
    }
}