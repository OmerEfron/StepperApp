package servlets.execution;

import StepperEngine.Flow.execute.ExecutionNotReadyException;
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

@WebServlet(name = "executionServlet", urlPatterns = "/execution")
public class ExecutionServlet extends HttpServlet {
    private static final String UUID_PARAMETER = "uuid";
    private static final String FREE_INPUT_PARAMETER = "free-input";
    private static final String FREE_INPUT_DATA_PARAMETER = "data";
    private static final String MISSING_PARAMETER_MESSAGE = "not flow name passed as parameter";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // gets an uuid of a new execution created by the engine.
        // must pass a flow name

        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String flowName = req.getParameter(ServletUtils.FLOW_NAME_PARAMETER);
        if(flowName != null){
            String newExecution = stepper.createNewExecution(flowName);
            ServletUtils.sendResponse(newExecution, newExecution.getClass(), resp);
        }
        else{
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(ServletUtils.FLOW_NAME_PARAMETER));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // execute a flow. gets a parameter the UUID of the execution.
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(UUID_PARAMETER);
        try {
            stepper.executeFlow(uuid);
            ServletUtils.sendResponse(MISSING_PARAMETER_MESSAGE, MISSING_PARAMETER_MESSAGE.getClass(), resp);
        } catch (ExecutionNotReadyException e) {
            ServletUtils.sendBadRequest(resp, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        Map<String, String> paramMap = null;
        try {
            paramMap = ServletUtils.getParamMap(req, UUID_PARAMETER, FREE_INPUT_PARAMETER, FREE_INPUT_DATA_PARAMETER);
        }catch (MissingParamException e){
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(e.getMissingParamName()));
        }
        if(paramMap != null) {
            boolean result = stepper.addFreeInputToExecution(paramMap.get(UUID_PARAMETER), paramMap.get(FREE_INPUT_PARAMETER), paramMap.get(FREE_INPUT_DATA_PARAMETER));
            if (result) {
                String successMessage = String.format("input %s added successfully", paramMap.get(FREE_INPUT_PARAMETER));
                ServletUtils.sendResponse(successMessage, successMessage.getClass(), resp);
            } else {
                ServletUtils.sendBadRequest(resp, String.format("cannot populate input %s with %s", paramMap.get(FREE_INPUT_PARAMETER),
                        paramMap.get(FREE_INPUT_DATA_PARAMETER)));
            }
        }
    }
}
