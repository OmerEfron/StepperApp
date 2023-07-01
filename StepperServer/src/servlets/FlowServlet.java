package servlets;

import DTO.FlowDetails.FlowDetails;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Stepper stepper = StepperUtils.getStepper(getServletContext());

        List<FlowDetails> flowDetailsList = stepper.getFlowsDetails();
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(flowDetailsList, new TypeToken<List<FlowDetails>>() {
        }.getType());
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonResponse);
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
