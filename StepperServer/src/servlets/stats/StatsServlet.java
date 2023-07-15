package servlets.stats;

import DTO.ExecutionsStatistics.FlowExecutionStats;
import StepperEngine.Stepper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "getStatsFromEngine", urlPatterns = "/stats")
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

    private void doGetForAllFlows(HttpServletResponse resp, Stepper stepper) throws IOException {
        List<FlowExecutionStats> flowExecutionStatsList=stepper.getFlowExecutionStatsList();
        ServletUtils.sendResponse(flowExecutionStatsList, flowExecutionStatsList.getClass(), resp);

    }

    private void doGetForSpecificFlow(String flowName, HttpServletResponse resp, Stepper stepper) throws IOException {
        FlowExecutionStats executionStats = stepper.getFlowExecutionsStats(flowName);

        if(executionStats != null){
            ServletUtils.sendResponse(executionStats, executionStats.getClass(), resp);
        }
        else{
            ServletUtils.sendBadRequest(resp, String.format("Cannot access stats of flow %s. flow might not exist," +
                    " or User dont have access to it", flowName));
        }
    }
}
