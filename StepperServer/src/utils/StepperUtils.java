package utils;

import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import jakarta.servlet.ServletContext;

public class StepperUtils {

    private static final String STEPPER_ATTRIBUTE_NAME = "stepper";
    private static final Object stepperLock = new Object();

    public static Stepper getStepper(ServletContext servletContext){
        Stepper stepper;
        synchronized (stepperLock){
            if(servletContext.getAttribute(STEPPER_ATTRIBUTE_NAME) == null){
                stepper = new Stepper();
                try {
                    stepper.load("C:\\Users\\Gil\\Desktop\\StepperApp2\\ex2.xml");
                } catch (ReaderException | FlowBuildException e) {
                    throw new RuntimeException(e);
                }
                servletContext.setAttribute(STEPPER_ATTRIBUTE_NAME,stepper);
            }
        }
        return (Stepper) servletContext.getAttribute(STEPPER_ATTRIBUTE_NAME);
    }
}
