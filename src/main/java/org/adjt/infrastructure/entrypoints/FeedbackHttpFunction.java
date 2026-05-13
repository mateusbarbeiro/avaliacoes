package org.adjt.infrastructure.entrypoints;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class FeedbackHttpFunction {
    @FunctionName("ReceberFeedback")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<String> request,
            final ExecutionContext context) {

//        // Repassa o JSON para o caso de uso persistir no banco
//        useCase.executar(request.getBody());

        return request.createResponseBuilder(HttpStatus.CREATED).body("Feedback salvo!").build();
    }
}
