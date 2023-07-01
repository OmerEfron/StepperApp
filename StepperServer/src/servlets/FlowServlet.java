package servlets;

import DTO.FlowDetails.FlowDetails;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import StepperEngine.StepperReader.XMLReadClasses.Flow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "getFlowsFromEngine", urlPatterns = {"/flows"})
public class FlowServlet extends HttpServlet {

    private final static String FLOW_PARAMETER_NAME = "flow_name";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String flowName = req.getParameter(FLOW_PARAMETER_NAME);
        if(flowName != null){
            doGetForSpecificFlow(resp, stepper, flowName);
        }
        else{
            doGetForAllFlows(resp, stepper);
        }
    }

    private void doGetForAllFlows(HttpServletResponse resp, Stepper stepper) throws IOException {
        List<FlowDetails> flowDetailsList = stepper.getFlowsDetails();
        sendResponse(flowDetailsList, flowDetailsList.getClass(), resp);
    }

    private void sendResponse(Object obj, Class<?> expectedClass, HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(obj, expectedClass);
        response.getWriter().write(jsonResponse);
    }
    private void doGetForSpecificFlow(HttpServletResponse resp, Stepper stepper, String flowName) throws IOException {
        FlowDetails flowDetails = stepper.getFlowsDetailsByName(flowName);
        if(flowDetails == null){
            ServletUtils.sendBadRequest(resp, String.format("flow %s is not exist", flowName));
        }
        else{
            sendResponse(flowDetails, FlowDetails.class, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String filePath = req.getParameter("new_flows");
        if(filePath!= null){
            try {
                synchronized (stepper) { // to prevent multiple threads accessing the load method.
                    stepper.load(filePath);
                }
            } catch (ReaderException | FlowBuildException e) {
                ServletUtils.sendBadRequest(resp, e.getMessage());
            }
        }
        resp.getWriter().write("loaded successfully");
    }


}
