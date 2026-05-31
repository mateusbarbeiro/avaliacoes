package org.adjt.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import jakarta.inject.Inject;
import org.adjt.services.RelatorioSemanalService;

import java.util.Arrays;

public class RelatorioTimerFunction {

    @Inject
    RelatorioSemanalService relatorioSemanalService;

    @FunctionName("GerarRelatorioSemanal")
    public void run(
            // Expressão CRON: Roda uma vez por semana (domingo à meia-noite)
            @TimerTrigger(name = "timerInfo", schedule = "0 0 0 * * 0") String timerInfo,
            final ExecutionContext context) {
        try {
            context.getLogger().info("Iniciando geração de relatório semanal...");
            relatorioSemanalService.gerarRelatorioSemanal();
            context.getLogger().info("Relatório semanal gerado e enviado com sucesso!");
        } catch (Exception e) {
            context.getLogger().severe("Erro inesperado ao gerar relatório semanal: " + e.getMessage());
            context.getLogger().severe(Arrays.toString(e.getStackTrace()));
        }
    }
}
