package org.adjt.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.adjt.entity.Avaliacao;

@ApplicationScoped
public class AvaliacaoService {
    @Transactional
    public Avaliacao salvarAvaliacao(Avaliacao avaliacao) {
        validaConsistenciaNota(avaliacao);
        avaliacao.persist();
        return avaliacao;
    }

    private static void validaConsistenciaNota(Avaliacao avaliacao) {
        if (avaliacao.nota < 0 || avaliacao.nota > 10)
            throw new IllegalArgumentException("A nota deve estar entre 0 e 10");
    }
}
