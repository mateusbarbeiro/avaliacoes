package org.adjt.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.QueueOutput;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.adjt.entity.Avaliacao;
import org.adjt.services.AvaliacaoService;

import java.util.Arrays;
import java.util.Optional;

public class FeedbackHttpFunction {
    
    @Inject
    AvaliacaoService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @FunctionName("ReceberFeedback")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req", 
                methods = {HttpMethod.POST}, 
                authLevel = AuthorizationLevel.ANONYMOUS
            ) HttpRequestMessage<Optional<String>> request,
            @QueueOutput(name = "filaSaida", queueName = "fila-aviso-urgencia", connection = "AzureWebJobsStorage") OutputBinding<String> queueOutput,
            final ExecutionContext context) {

        context.getLogger().info("Iniciando processamento de nova avaliação via POST.");

        try {
            // Obter o corpo da requisição como String
            Optional<String> bodyOpt = request.getBody();

            if (bodyOpt.isEmpty() || bodyOpt.get().trim().isEmpty())
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("O corpo da requisição não pode estar vazio.")
                        .build();

            Avaliacao avaliacao = objectMapper.readValue(bodyOpt.get(), Avaliacao.class);

            if (avaliacao.nota == null)
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Nota é obrigatória.")
                        .build();

            // Validações básicas
            if (avaliacao.descricao == null || avaliacao.descricao.trim().isEmpty())
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Descrição é obrigatória.")
                        .build();

            // Salvar a avaliação
            Avaliacao avaliacaoSalva = service.salvarAvaliacao(avaliacao);

            objectMapper.registerModule(new JavaTimeModule());
            String jsonResposta = objectMapper.writeValueAsString(avaliacaoSalva);

            if (avaliacaoSalva.isDeverAvisar()) {
                queueOutput.setValue(jsonResposta);
                context.getLogger().info("Avaliação crítica enfileirada com sucesso.");
            }

            return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(jsonResposta)
                    .build();
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            context.getLogger().warning("JSON inválido: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Formato JSON inválido: " + e.getMessage())
                    .build();

        } catch (IllegalArgumentException e) {
            context.getLogger().warning("Avaliação inválida: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Avaliação inválida: " + e.getMessage())
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Erro ao processar avaliação: " + e.getMessage());
            context.getLogger().severe(Arrays.toString(e.getStackTrace()));
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao processar a avaliação.")
                    .build();
        }
    }
}