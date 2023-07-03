package servlets;

import DTO.ExecutionsStatistics.api.FlowExecutionStatsDefinition;
import StepperEngine.Stepper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
import java.util.List;

public class StatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String flowName = req.getParameter(ServletUtils.FLOW_NAME_PARAMETER);
        if(flowName != null){
            doGetForSpecificFlow(flowName, resp, stepper);
        }
        else{
            doGetForAllFlows(resp, stepper);
        }
    }

    private void doGetForAllFlows(HttpServletResponse resp, Stepper stepper) {

    }

    private void doGetForSpecificFlow(String flowName, HttpServletResponse resp, Stepper stepper) throws IOException {

        FlowExecutionStatsDefinition executionStats = stepper.getFlowExecutionsStats(flowName);
        if(executionStats != null){
            ServletUtils.sendResponse(executionStats, executionStats.getClass(), resp);
        }
        else{
            ServletUtils.sendBadRequest(resp, String.format("Cannot access stats of flow %s. flow might not exist," +
                    " or user dont have access to it", flowName));
        }
    }
}
