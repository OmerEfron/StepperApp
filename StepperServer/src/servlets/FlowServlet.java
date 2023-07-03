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
import java.io.InputStream;
import java.util.List;

@WebServlet(name = "getFlowsFromEngine", urlPatterns = {"/flows"})
public class FlowServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String flowName = req.getParameter(ServletUtils.FLOW_NAME_PARAMETER);
        if(flowName != null){
            doGetForSpecificFlow(resp, stepper, flowName);
        }
        else{
            doGetForAllFlows(resp, stepper);
        }
    }

    private void doGetForAllFlows(HttpServletResponse resp, Stepper stepper) throws IOException {
        List<FlowDetails> flowDetailsList = stepper.getFlowsDetails();
        ServletUtils.sendResponse(flowDetailsList, flowDetailsList.getClass(), resp);
    }

    private void doGetForSpecificFlow(HttpServletResponse resp, Stepper stepper, String flowName) throws IOException {
        FlowDetails flowDetails = stepper.getFlowsDetailsByName(flowName);
        if(flowDetails == null){
            ServletUtils.sendBadRequest(resp, String.format("flow %s is not exist", flowName));
        }
        else{
            ServletUtils.sendResponse(flowDetails, flowDetails.getClass(), resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        InputStream inputStream = req.getInputStream();
        if(inputStream != null){
            try {
                synchronized (stepper) { // to prevent multiple threads accessing the load method.
                    stepper.load(inputStream);
                }
            } catch (ReaderException | FlowBuildException e) {
                ServletUtils.sendBadRequest(resp, e.getMessage());
            }
        }
        resp.getWriter().write("loaded successfully");
    }


}
