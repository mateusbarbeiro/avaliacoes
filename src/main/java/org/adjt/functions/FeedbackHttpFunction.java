package org.adjt.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import jakarta.inject.Inject;
import org.adjt.entity.Avaliacao;
import org.adjt.services.AvaliacaoService;

import java.util.Arrays;
import java.util.Optional;

public class FeedbackHttpFunction {
    @Inject
    AvaliacaoService service;

    @FunctionName("ReceberFeedback")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Avaliacao>> request,
            final ExecutionContext context) {

        context.getLogger().info("Iniciando processamento de nova avaliação via POST.");

        Optional<Avaliacao> avaliacaoOpt = request.getBody();

        if (avaliacaoOpt.isEmpty())
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("O corpo da requisição não pode estar vazio.")
                    .build();

        try {
            Avaliacao avaliacao = service.salvarAvaliacao(avaliacaoOpt.get());

            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(avaliacao)
                    .build();
        } catch (IllegalArgumentException e) {
            context.getLogger().warning("Avaliação inválida: " + e.getMessage());

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Avaliação inválida: " + e.getMessage())
                    .build();
        }
        catch (Exception e) {
            context.getLogger().severe("Erro ao salvar no banco: " + e.getMessage());
            context.getLogger().severe(Arrays.toString(e.getStackTrace()));

            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao processar a avaliação.")
                    .build();
        }
    }
}
