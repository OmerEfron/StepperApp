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

import static utils.ServletUtils.UUID_PARAMETER;

@WebServlet(name = "executionStatus", urlPatterns = "/execution/status")
public class ExecutionStatusServlet extends HttpServlet {



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        Stepper stepper = StepperUtils.getStepper(getServletContext());
        try {
            Map<String, String> paramMap = ServletUtils.getParamMap(req, UUID_PARAMETER);
            String uuid = paramMap.get(UUID_PARAMETER);
            Boolean isEnded = stepper.isExecutionEnded(uuid);
            ServletUtils.sendResponse(isEnded, isEnded.getClass(), resp);
        }catch (MissingParamException e){
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(e.getMissingParamName()));
        }


    }
}
