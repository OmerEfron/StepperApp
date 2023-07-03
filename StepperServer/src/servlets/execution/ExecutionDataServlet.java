package servlets.execution;

import DTO.FlowExecutionData.api.FlowExecutionData;
import DTO.FlowExecutionData.impl.FlowExecutionDataImpl;
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

@WebServlet(name = "executionDataServlet", urlPatterns = "/execution/data")
public class ExecutionDataServlet extends HttpServlet {

    private static final String UUID_PARAMETER = "uuid";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        String uuid = req.getParameter(UUID_PARAMETER);
        String flow_name = req.getParameter(ServletUtils.FLOW_NAME_PARAMETER);
        if(uuid != null){
            FlowExecutionData executionData = stepper.getFlowExecutionData(uuid);
            ServletUtils.sendResponse(executionData, executionData.getClass(), resp);
        }
        else if(flow_name != null){
            List<FlowExecutionData> flowExecutionDataList = stepper.getFlowExecutionDataMap().get(flow_name);
            ServletUtils.sendResponse(flowExecutionDataList, flowExecutionDataList.getClass(), resp);
        }
        else{
            List<FlowExecutionDataImpl> flowExecutionDataList = stepper.getFlowExecutionDataList();
            ServletUtils.sendResponse(flowExecutionDataList, flowExecutionDataList.getClass(), resp);
        }
    }
}
