package org.adjt.dto;

import org.adjt.entity.NivelCriticidade;

import java.time.LocalDateTime;
import java.util.Map;

public record RelatorioSemanalDto(
        String descricao,
        NivelCriticidade urgencia,
        LocalDateTime dataGeracao,
        Map<String, Long> quantidadeAvaliacoesPorDia,
        Map<String, Long> quantidadeAvaliacoesPorUrgencia
) {
}
