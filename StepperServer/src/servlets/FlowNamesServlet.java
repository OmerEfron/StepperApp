package servlets;

import StepperEngine.Stepper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "getFlowsFromEngine2", urlPatterns = "/flows_names")

public class FlowNamesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());
        if(stepper != null){
            List<String> flowNames = stepper.getFlowNames();
            ServletUtils.sendResponse(flowNames,flowNames.getClass(),resp);
        }
    }
}
