package org.adjt.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.adjt.dto.RelatorioSemanalDto;
import org.adjt.entity.NivelCriticidade;
import org.adjt.repository.AvaliacaoRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@ApplicationScoped
public class RelatorioSemanalService {
    @Inject
    MailSender mailSender;

    @ConfigProperty(name = "admin.email")
    String adminEmailNotificado;

    @Inject
    AvaliacaoRepository avaliacaoRepository;

    public void gerarRelatorioSemanal() {
        RelatorioSemanalDto relatorioDto = compilarDadosRelatorio();
        gerarRelatorio(relatorioDto);
    }

    private RelatorioSemanalDto compilarDadosRelatorio() {
        LocalDateTime dataLimite = LocalDateTime.now().minusWeeks(1);
        Map<String, Long> qtdPorUrgencia = avaliacaoRepository.buscarAgrupadoPorUrgencia(dataLimite);
        Map<String, Long> qtdPorDia = avaliacaoRepository.buscarAgrupadoPorDia(dataLimite);
        Double mediaNota = avaliacaoRepository.mediaNotaPeriodo(dataLimite);

        return new RelatorioSemanalDto(
                "Relatório semanal avaliações",
                NivelCriticidade.getDadoNota(mediaNota),
                LocalDateTime.now(),
                qtdPorDia,
                qtdPorUrgencia
        );
    }

    private void gerarRelatorio(RelatorioSemanalDto relatorioDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataFormatada = relatorioDto.dataGeracao().format(formatter);

        // Construindo o corpo do e-mail em HTML
        StringBuilder html = new StringBuilder();
        html.append("<h2>Relatório Semanal de Avaliações</h2>");
        html.append("<p><strong>Data de Geração:</strong> ").append(dataFormatada).append("</p>");
        html.append("<p><strong>Descrição:</strong> ").append(relatorioDto.descricao()).append("</p>");
        html.append("<p><strong>Nível de Urgência Média:</strong> ").append(relatorioDto.urgencia()).append("</p>");

        html.append("<h3>Avaliações por Dia:</h3><ul>");
        relatorioDto.quantidadeAvaliacoesPorDia().forEach((dia, quantidade) ->
                html.append("<li>").append(dia).append(": ").append(quantidade).append("</li>")
        );
        html.append("</ul>");

        html.append("<h3>Avaliações por Urgência:</h3><ul>");
        relatorioDto.quantidadeAvaliacoesPorUrgencia().forEach((urgencia, quantidade) ->
                html.append("<li>").append(urgencia).append(": ").append(quantidade).append("</li>")
        );
        html.append("</ul>");

        mailSender.sendEmailHtml(
                adminEmailNotificado,
                "Tech Challenge: Relatório Semanal de Avaliações",
                html.toString());
    }
}
