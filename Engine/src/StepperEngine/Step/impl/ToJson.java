package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.Enumeration.ZipEnumerator;
import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class ToJson extends StepDefinitionAbstract {
    public ToJson(){
        super("To Json",true);
        addInput(new DataDefinitionDeclarationImpl("CONTENT","Content", DataNecessity.MANDATORY, DataDefinitionRegistry.STRING));
        addOutput(new DataDefinitionDeclarationImpl("JSON", "Json representation", DataNecessity.NA, DataDefinitionRegistry.JSON));

    }
    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName) {
        Instant start = Instant.now();
        Gson gson=new Gson();
        String invokeSummery;
        String content = context.getDataValue(nameToAlias.get("CONTENT"), String.class);
        JsonObject jsonObject =null;
        try {
            jsonObject = gson.fromJson(content, JsonObject.class);
            invokeSummery="The content converted to Json!";
            context.addLog(stepName,"Content is JSON string. Converting it to json…");
            context.setStepStatus(stepName,StepStatus.SUCCESS);
        } catch (JsonSyntaxException e) {
            context.addLog(stepName,"Content is not a valid JSON representation");
            invokeSummery="Content is not a valid JSON representation.";
            context.setStepStatus(stepName,StepStatus.FAIL);
        }
        context.setInvokeSummery(stepName, invokeSummery);
        context.storeValue(nameToAlias.get("JSON"),jsonObject);
        context.setTotalTime(stepName, Duration.between(start, Instant.now()));
        return context.getStepStatus(stepName);
    }

}
