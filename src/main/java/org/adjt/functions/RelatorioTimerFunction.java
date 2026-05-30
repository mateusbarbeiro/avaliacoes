package org.adjt.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import jakarta.transaction.Transactional;

public class RelatorioTimerFunction {

    @Transactional
    @FunctionName("GerarRelatorioSemanal")
    public void run(
            // Expressão CRON: Roda uma vez por semana (domingo à meia-noite)
            @TimerTrigger(name = "timerInfo", schedule = "0 0 0 * * 0") String timerInfo,
            final ExecutionContext context) {

        context.getLogger().info("Iniciando geração de relatório semanal...");
    }
}
