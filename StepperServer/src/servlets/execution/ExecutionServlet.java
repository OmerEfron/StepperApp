package servlets.execution;

import DTO.DataAndType;
import StepperEngine.Flow.execute.ExecutionNotReadyException;
import StepperEngine.Stepper;
import com.google.gson.Gson;
import exceptions.MissingParamException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StatsManager;
import utils.StepperUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "executionServlet", urlPatterns = "/execution")
public class ExecutionServlet extends HttpServlet {
    private static final String UUID_PARAMETER = "uuid";
    private static final String FREE_INPUT_PARAMETER = "free-input";
    private static final String FREE_INPUT_DATA_PARAMETER = "data";

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
        StatsManager statsManager = ServletUtils.getStatsManager(getServletContext());
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(UUID_PARAMETER);
        if(uuid != null) {
            try {
                stepper.executeFlow(uuid);
                while (!stepper.isExecutionEnded(uuid)) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                synchronized (getServletContext()) {
                    statsManager.setFlowExecutionStatsList(stepper.getFlowExecutionStatsList());
                    statsManager.addVersion();
                }
                ServletUtils.sendResponse(Boolean.TRUE, Boolean.class, resp);
            } catch (ExecutionNotReadyException e) {
                ServletUtils.sendBadRequest(resp, e.getMessage());
            }
        }
        else{
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(UUID_PARAMETER));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        Map<String, String> paramMap = null;
        try {
            paramMap = ServletUtils.getParamMap(req, UUID_PARAMETER, FREE_INPUT_PARAMETER, FREE_INPUT_DATA_PARAMETER);
            String dataParam = paramMap.get(FREE_INPUT_DATA_PARAMETER);
            Boolean result;
            try{
                result = stepper.addFreeInputToExecution(paramMap.get(UUID_PARAMETER), paramMap.get(FREE_INPUT_PARAMETER), Integer.valueOf(dataParam));
            }catch (NumberFormatException e){
                try {
                    result = stepper.addFreeInputToExecution(paramMap.get(UUID_PARAMETER), paramMap.get(FREE_INPUT_PARAMETER), Double.valueOf(dataParam));
                }catch (NumberFormatException e1){
                    result = stepper.addFreeInputToExecution(paramMap.get(UUID_PARAMETER), paramMap.get(FREE_INPUT_PARAMETER), dataParam);
                }
            }
            ServletUtils.sendResponse(result, result.getClass(), resp);
        }catch (MissingParamException e){
            ServletUtils.sendBadRequest(resp, ServletUtils.getMissingParameterMessage(e.getMissingParamName()));
        }
    }
}
