package StepperEngine.StepperReader.api;

import StepperEngine.StepperReader.XMLReadClasses.TheStepper;
import StepperEngine.StepperReader.Exception.ReaderException;

import java.io.InputStream;

public interface StepperReader {
    TheStepper read(String filePath)throws ReaderException;
    TheStepper read(InputStream inputStream) throws ReaderException;
}
