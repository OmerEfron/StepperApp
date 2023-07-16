package servlets;

import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import users.roles.RoleImpl;
import utils.ServletUtils;
import utils.StepperUtils;

import java.io.*;
import java.util.*;


@WebServlet("/upload-file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        Collection<Part> parts = request.getParts();

        InputStream inputStream=null ;
        String filePath=null;
        for (Part part : parts) {
            if(part.getName().equals("file"))
                inputStream=part.getInputStream();
            else
                filePath=readFromInputStream(part.getInputStream());
        }
        try {
            Stepper stepper = StepperUtils.getStepper(getServletContext());
            if(!StepperUtils.isStepperIn(getServletContext())) {
                stepper.loadAllStepper(inputStream, filePath);
                StepperUtils.setStepperIn(getServletContext());
                StepperUtils.getRolesManger(getServletContext());
            }else{
                stepper.addFlowsFromFile(inputStream,filePath);
                updateStatsManager(stepper);
                //Map<String, RoleImpl> stringRoleMap = StepperUtils.getRolesMap(getServletContext());
            }


        }catch (ReaderException | FlowBuildException | RuntimeException e ){
            ServletUtils.sendBadRequest(response,e.getMessage());

        }

    }

    private void updateStatsManager(Stepper stepper) {
        ServletUtils.getStatsManager(getServletContext()).addVersion();
        ServletUtils.getStatsManager(getServletContext()).setFlowExecutionStatsList(stepper.getFlowExecutionStatsList());
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
