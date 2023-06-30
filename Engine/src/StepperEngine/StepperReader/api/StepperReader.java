package StepperEngine.StepperReader.api;

import StepperEngine.StepperReader.XMLReadClasses.TheStepper;
import StepperEngine.StepperReader.Exception.ReaderException;

public interface StepperReader {
    TheStepper read(String filePath)throws ReaderException;
}
