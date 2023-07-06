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
            Stepper stepper= StepperUtils.getStepper(getServletContext());
            stepper.load2(inputStream,filePath);
        }catch (ReaderException | FlowBuildException | RuntimeException e ){
            ServletUtils.sendBadRequest(response,e.getMessage());
        }

    }
    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
