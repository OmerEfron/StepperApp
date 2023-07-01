package StepperEngine;

import DTO.ExecutionsStatistics.api.FlowExecutionStatsDefinition;
import DTO.ExecutionsStatistics.impl.FlowExecutionStatsImpl;
import DTO.FlowDetails.FlowDetails;
import DTO.FlowDetails.FlowDetailsImpl;
import DTO.FlowExecutionData.api.FlowExecutionData;
import DTO.FlowExecutionData.impl.FlowExecutionDataImpl;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Flow.api.FlowDefinitionImpl;
import StepperEngine.Flow.api.FlowDefinition;
import StepperEngine.Flow.execute.ExecutionNotReadyException;
import StepperEngine.Flow.execute.FlowExecution;

import StepperEngine.Flow.execute.runner.FlowExecutor;

import StepperEngine.StepperReader.Exception.ReaderException;
import StepperEngine.StepperReader.XMLReadClasses.Continuation;
import StepperEngine.StepperReader.XMLReadClasses.ContinuationMapping;
import StepperEngine.StepperReader.XMLReadClasses.Flow;
import StepperEngine.StepperReader.XMLReadClasses.TheStepper;
import StepperEngine.StepperReader.api.StepperReader;
import StepperEngine.StepperReader.impl.StepperReaderFromXml;


import java.io.FileInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * a class that repesents the engine of the system.
 */
public class Stepper implements Serializable {
    private List<FlowDefinition> flows = new ArrayList<>();

    private List<String> flowNames = new ArrayList<>();

    private Map<String, FlowDefinition> flowsMap = new HashMap<>();

    private Map<Integer, String> flowsByNumber = new LinkedHashMap<>();

    private Map<String,List<String>> continuationMap=new HashMap<>();
    private Map<String, FlowDetails> flowDetailsMap=new HashMap<>();
    private final Map<String, FlowExecution> executionsMap = new HashMap<>();
    private final Map<String, List<FlowExecution>> executionsPerFlow = new HashMap<>();

    private ExecutorService executorService;
    private List<FlowExecutionDataImpl> flowExecutionDataList=new ArrayList<>();




    public Stepper() {

    }

    public void load(String filePath) throws ReaderException, FlowBuildException {
        StepperReader reader = new StepperReaderFromXml();
        TheStepper theStepper = reader.read(filePath);
        newFlows(theStepper);
    }

    public void newFlows(TheStepper stepper) throws FlowBuildException {
        List<FlowDefinition> newFlows = new ArrayList<>();
        Map<String, FlowDefinition> newFlowsMap = new HashMap<>();
        checkForDuplicateNames(stepper);
        checkForDuplicateOutputs(stepper);
        ExecutorService executorService = Executors.newFixedThreadPool(stepper.getThreadPool());
        stepper.getFlows().getFlows().stream()
                .forEach(flow -> {
                    try {
                        newFlows.add(new FlowDefinitionImpl(flow));
                    } catch (FlowBuildException e) {
                        throw new RuntimeException(e);
                    }
                });
        this.executorService = executorService;
        flows = newFlows;
        flows.forEach(flow -> newFlowsMap.put(flow.getName(), flow));
        flowsMap = newFlowsMap;
        flowsByNumber = IntStream.range(0, flows.size())
                .boxed()
                .collect(Collectors.toMap(i -> i + 1, i -> flows.get(i).getName()));
        flowNames = flows.stream()
                .map(FlowDefinition::getName)
                .collect(Collectors.toList());
        for(FlowDefinition flow :flows){
            flowDetailsMap.put(flow.getName(),new FlowDetailsImpl(flow));
        }
        try {
            checkContinuation();
        } catch (FlowBuildException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * The function checks that the continuity setting is correct(steps names ,inputs names,outputs names)
     * @throws FlowBuildException
     */
    private void checkContinuation() throws FlowBuildException{
        for (FlowDefinition flow:flows){
            if (flow.hasContinuation())
            {
                for(Continuation continuation:flow.getContinuation()) {
                    try {
                        checkIfContinuationValid(flow, continuation);
                        if(!continuationMap.containsKey(flow.getName()))
                            continuationMap.put(flow.getName(),new ArrayList<>());
                        continuationMap.get(flow.getName()).add(continuation.getTargetFlow());
                    }catch (FlowBuildException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void checkIfContinuationValid(FlowDefinition flow, Continuation continuation) throws FlowBuildException {
        if (!flowsMap.containsKey(continuation.getTargetFlow()))
            throw new FlowBuildException("The flow " + continuation.getTargetFlow() + " doesn't exist", flow.getName());
        else
        {
            try {

                for(ContinuationMapping continuationMapping:continuation.getContinuationMappings()){
                    flowsMap.get(flow.getName()).isDDExist(continuationMapping);
                    flowsMap.get(continuation.getTargetFlow()).isInputExist(continuationMapping);
                    if(!flow.getAllDataDefinitions().get(continuationMapping.getSourceData()).dataDefinition().getType()
                            .equals(flowsMap.get(continuation.getTargetFlow()).getFreeInputByName(continuationMapping.getTargetData()).dataDefinition().getType())){
                        throw new FlowBuildException(flow.getName(),"Can't create continuation mapping between: "
                        + continuationMapping.getSourceData()+" and "+continuationMapping.getTargetData()+".\n" +
                                "Are not the same type!");
                    }
                    else {
                        flowsMap.get(continuation.getTargetFlow()).addContinuationMapping(flow.getDDByName(continuationMapping.getSourceData()).getFullQualifiedName(),continuationMapping.getTargetData());
                    }
                }
            }catch (FlowBuildException e) {
                throw e;
            }
        }
    }

    public boolean isFlowExist(String name) {
        return flowNames.contains(name);
    }

    public List<String> getContinuationList (String flowName){
        return continuationMap.get(flowName);
    }

    public String applyContinuation(String pastFlowUUID,String continuationFlow) {
        FlowExecution pastFlow=executionsMap.get(pastFlowUUID);
        FlowExecution flowExecution=new FlowExecution(flowsMap.get(continuationFlow));
        flowExecution.applyContinuation(pastFlow);
        executionsMap.put(flowExecution.getUUID(),flowExecution);
        return flowExecution.getUUID();
    }
    public String reRunFlow(String pastFlowUUID){
        FlowExecution pastExecution=executionsMap.get(pastFlowUUID);
        FlowExecution flowExecution=new FlowExecution(flowsMap.get(pastExecution.getFlowDefinition().getName()));
        flowExecution.updateFreeInputsValue(pastExecution);
        executionsMap.put(flowExecution.getUUID(),flowExecution);
        return flowExecution.getUUID();

    }
        public List<String> getFlowNames() {
        return flowNames;
    }

    public Integer getNumOfFlows() {
        return flows.size();
    }

    public Map<Integer, String> getFlowsByNumber() {
        return flowsByNumber;
    }


    private void checkForDuplicateOutputs(TheStepper stepper) throws FlowBuildException {
        String duplicateOutput;
        for (Flow flow : stepper.getFlows().getFlows()) {
            if ((duplicateOutput = checkForDuplicatesOutputsInFlow(flow)) != null)
                throw new FlowBuildException("Duplicate output names: " + duplicateOutput, flow.getName());
        }
    }

    public String checkForDuplicatesOutputsInFlow(Flow flow) {
        Set<String> uniqueOutputs = new HashSet<>();
        for (String output : flow.getFlowOutput().split(",")) {
            if (!uniqueOutputs.add(output))
                return output;
        }
        return null;
    }


    private static void checkForDuplicateNames(TheStepper stepper) throws FlowBuildException {
        String duplicate;
        if ((duplicate = findDuplicateFlowName(stepper)) != null) {
            throw new FlowBuildException("Duplicate flow error. " + duplicate + " is already exist", duplicate);
        }
    }

    private static String findDuplicateFlowName(TheStepper stepper) {
        Set<String> flowNames = new HashSet<>();
        for (Flow flow : stepper.getFlows().getFlows()) {
            if (!flowNames.add(flow.getName())) {
                return flow.getName();
            }
        }
        return null;
    }

    public FlowExecution getFlowExecution(String flowName) {
        if (!isFlowExist(flowName)) {
            return null;
        }
        return new FlowExecution(flowsMap.get(flowName));
    }



    public void executeFlow(String uuid) throws ExecutionNotReadyException {
        FlowExecutor flowExecutor = new FlowExecutor();
        synchronized (this) {
            FlowExecution flowExecution = executionsMap.get(uuid);
            if (flowExecution.isCanBeExecuted()) {
                executorService.submit(() -> {
                    exectionTask(flowExecutor, flowExecution);
                    flowExecutionDataList.add(new FlowExecutionDataImpl(flowExecution));
                });

            } else
                throw new ExecutionNotReadyException("flow is not ready to be executed. check for not provided" +
                        " free inputs", uuid);
        }
    }

    private void exectionTask(FlowExecutor flowExecutor, FlowExecution flowExecution) {
        flowExecutor.executeFlow(flowExecution);
        executionsPerFlow.computeIfAbsent(
                flowExecution.getFlowDefinition().getName(),
                k -> new ArrayList<>()
        ).add(flowExecution);
    }



    public List<FlowDetails> getFlowsDetails() {
        return new ArrayList<>(flowDetailsMap.values());
    }

    public FlowDetails buildShowFlow(String flowName) {
        return new FlowDetailsImpl(flowsMap.get(flowName));
    }

    public String getNamesOfFlowsToPrint() {
        return IntStream.range(0, flows.size())
                .mapToObj(i -> (i + 1) + "." + flows.get(i).getName() + (i == flows.size() - 1 ? "" : "\n"))
                .collect(Collectors.joining());
    }

    public boolean addFreeInputToExecution(String uuid, String dataName, Object value) {
        FlowExecution flowExecution = executionsMap.get(uuid);
        if (flowExecution != null) {
            return flowExecution.addFreeInput(dataName, value);
        }
        return false;
    }

    public String createNewExecution(String flowName) {
        String uuid;
        if (flowsMap.get(flowName) != null) {
            FlowExecution flowExecution = new FlowExecution(flowsMap.get(flowName));
            uuid = flowExecution.getUUID();
            executionsMap.put(flowExecution.getUUID(), flowExecution);
        } else {
            uuid = null;
        }
        return uuid;
    }

    public boolean getExecutionReadyToBeExecuteStatus(String uuid) {
        FlowExecution flowExecution = executionsMap.get(uuid);
        if (uuid != null) {
            return flowExecution.isCanBeExecuted();
        } else
            return false;
    }

    public FlowExecution getFlowExecutionByUuid(String uuid){
        synchronized (this) {
            return executionsMap.get(uuid);
        }
    }

    public boolean getExecutionStatus(String uuid) {
        FlowExecution flowExecution = getFlowExecutionByUuid(uuid);
        if (flowExecution != null) {
            return flowExecution.hasExecuted();
        }
        return false;
    }

    public double getExecutionPartialStatus(String uuid) {
        synchronized (this) {
            FlowExecution flowExecution = getFlowExecutionByUuid(uuid);
            return (double) (flowExecution.getNumOfStepsExecuted() / flowExecution.getNumOfSteps());
        }
    }

    public List<FlowExecutionDataImpl> getFlowExecutionDataList() {
        return flowExecutionDataList;
    }

    public FlowExecutionData getFlowExecutionData(String uuid){
        FlowExecution flowExecution = executionsMap.get(uuid);
        if(flowExecution!=null){
            if(flowExecution.hasExecuted())
                return new FlowExecutionDataImpl(flowExecution);
        }
        return null;
    }

    public FlowExecutionStatsDefinition getFlowExecutionsStats(String flowName) {
        synchronized (this) {
            return new FlowExecutionStatsImpl(flowsMap.get(flowName), executionsPerFlow.get(flowName));
        }
    }

    public FlowDetails getFlowsDetailsByName(String flowToContinue) {
        return flowDetailsMap.get(flowToContinue);
    }
}
