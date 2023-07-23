package StepperEngine.Step.impl;

import StepperEngine.DataDefinitions.Enumeration.MethodEnumerator;
import StepperEngine.DataDefinitions.Enumeration.ProtocolEnumerator;
import StepperEngine.DataDefinitions.impl.DataDefinitionRegistry;
import StepperEngine.Flow.execute.context.StepExecutionContext;
import StepperEngine.Step.api.DataDefinitionDeclarationImpl;
import StepperEngine.Step.api.DataNecessity;
import StepperEngine.Step.api.StepDefinitionAbstract;
import StepperEngine.Step.api.StepStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class HTTPCall extends StepDefinitionAbstract {

    public HTTPCall (){
        super("HTTP Call",false);
        this.addInput(new DataDefinitionDeclarationImpl("RESOURCE","Resource Name (include query parameters)", DataNecessity.MANDATORY, DataDefinitionRegistry.STRING));
        this.addInput(new DataDefinitionDeclarationImpl("ADDRESS","Domain:Port", DataNecessity.MANDATORY, DataDefinitionRegistry.STRING));
        this.addInput(new DataDefinitionDeclarationImpl("PROTOCOL","protocol", DataNecessity.MANDATORY, DataDefinitionRegistry.PROTOCOL_ENUMERATION));
        this.addInput(new DataDefinitionDeclarationImpl("METHOD","Method", DataNecessity.MANDATORY, DataDefinitionRegistry.METHOD_ENUMERATION));
        this.addInput(new DataDefinitionDeclarationImpl("BODY","Request Body", DataNecessity.MANDATORY, DataDefinitionRegistry.JSON));
        this.addOutput(new DataDefinitionDeclarationImpl("CODE","Response code",DataNecessity.NA,DataDefinitionRegistry.NUMBER));
        this.addOutput(new DataDefinitionDeclarationImpl("RESPONSE_BODY","Response body",DataNecessity.NA,DataDefinitionRegistry.STRING));

    }
    @Override
    public StepStatus invoke(StepExecutionContext context, Map<String, String> nameToAlias, String stepName) {
        String resource=context.getDataValue("RESOURCE",String.class);
        String address=context.getDataValue("ADDRESS",String.class);
        ProtocolEnumerator protocol=context.getDataValue("PROTOCOL",ProtocolEnumerator.class);
        MethodEnumerator method= Optional.ofNullable(context.getDataValue("METHOD", MethodEnumerator.class)).orElse(MethodEnumerator.GET);
        JsonObject body=context.getDataValue("BODY",JsonObject.class);
        String finalUrl;
        Request request=null;
        OkHttpClient client = new OkHttpClient();

        if(protocol.equals(ProtocolEnumerator.HTTPS)){
            finalUrl="https://"+address+resource;
        } else {
            finalUrl="http://"+address+resource;
        }

        if(body==null){
            if(method.equals(MethodEnumerator.DELETE)){
                request=new Request.Builder()
                        .url(finalUrl)
                        .delete()
                        .build();
            }
            else if (method.equals(MethodEnumerator.PUT) || method.equals(MethodEnumerator.POST))
            {
                context.setStepStatus(stepName, StepStatus.FAIL);
                context.setInvokeSummery(stepName, "Cannt create put/post request without body!!");
                return StepStatus.FAIL;
            }
        }else {
            String jsonBody = new Gson().toJson(body);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(jsonBody, mediaType);
            request=getRequest(finalUrl,method,requestBody);
        }

        try {
            String[] domainAndPort=address.split(":");
            context.addLog(stepName,"About to invoke http request " + protocol.getStringValue()+" | "+method+ " | "+domainAndPort[0]+" | "+domainAndPort[1]);
            Response response = client.newCall(request).execute();
            context.addLog(stepName,"Received Response. Status code: "+response.code());
            context.storeValue("CODE",response.code());
            context.storeValue("RESPONSE_BODY",response.body().string());
            context.setInvokeSummery(stepName,"The request was sent successfully");
            context.setStepStatus(stepName,StepStatus.SUCCESS);
            return StepStatus.SUCCESS;
        } catch (IOException e) {
            context.setStepStatus(stepName, StepStatus.FAIL);
            context.setInvokeSummery(stepName, "The request failed :"+ e.getMessage());
            return StepStatus.FAIL;
        }

    }

    private Request getRequest(String finalUrl, MethodEnumerator method, RequestBody body) {
        Request request = null;
        switch (method){
            case GET:
                request=new Request.Builder()
                        .url(finalUrl)
                        .build();
                break;
            case DELETE:
                request=new Request.Builder()
                        .url(finalUrl)
                        .delete(body)
                        .build();
                break;
            case PUT:
                request=new Request.Builder()
                        .url(finalUrl)
                        .put(body)
                        .build();
                break;
            case POST:
                request=new Request.Builder()
                        .url(finalUrl)
                        .post(body)
                        .build();
                break;
        }
        return request;
    }
}
