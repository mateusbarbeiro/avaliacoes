package org.adjt.functions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import jakarta.inject.Inject;
import org.adjt.services.AvisoUrgenciaService;

public class NotificarQueueFunction {

    @Inject
    AvisoUrgenciaService avisoUrgenciaService;

    @FunctionName("NotificarCriticos")
    public void run(
            // Gatilho ouvindo uma fila específica na Azure
            @QueueTrigger(name = "msg", queueName = "fila-aviso-urgencia", connection = "AzureWebJobsStorage") String mensagemQueue,
            final ExecutionContext context) {
        try {
            context.getLogger().info("Processando feedback crítico da fila. " + mensagemQueue);
            avisoUrgenciaService.notificarAvisoUrgencia(mensagemQueue);
            context.getLogger().info("E-mail de urgência enviado com sucesso!");
        } catch (JsonProcessingException e) {
            context.getLogger().severe("Erro ao processar JSON da mensagem da fila: " + e.getMessage() + "\n" + mensagemQueue);
        } catch (Exception e) {
            context.getLogger().severe("Erro inesperado ao processar mensagem da fila: " + e.getMessage() + "\n" + mensagemQueue);
        }
    }
}
