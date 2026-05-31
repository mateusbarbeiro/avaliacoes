package org.adjt.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.adjt.entity.Avaliacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AvaliacaoRepository implements PanacheRepository<Avaliacao> {

    @Inject
    EntityManager entityManager;

    public Map<String, Long> buscarAgrupadoPorUrgencia(LocalDateTime dataLimite) {
        String jpql = """
                SELECT
                    CASE
                        WHEN a.nota > 7 THEN 'NORMAL'
                        WHEN a.nota > 5 THEN 'URGENTE'
                        ELSE 'CRITICO'
                    END,
                    COUNT(a)
                FROM Avaliacao a
                WHERE a.dataCriacao >= :dataLimite
                GROUP BY
                    CASE
                        WHEN a.nota > 7 THEN 'NORMAL'
                        WHEN a.nota > 5 THEN 'URGENTE'
                        ELSE 'CRITICO'
                    END
                """;
        return converterParaMap(entityManager.createQuery(jpql, Object[].class)
                .setParameter("dataLimite", dataLimite)
                .getResultList());
    }

    public Map<String, Long> buscarAgrupadoPorDia(LocalDateTime dataLimite) {
        String sql =  """
                SELECT
                    CASE trim(to_char(a.dataCriacao, 'Day'))
                        WHEN 'Monday' THEN 'Segunda-feira'
                        WHEN 'Tuesday' THEN 'Terça-feira'
                        WHEN 'Wednesday' THEN 'Quarta-feira'
                        WHEN 'Thursday' THEN 'Quinta-feira'
                        WHEN 'Friday' THEN 'Sexta-feira'
                        WHEN 'Saturday' THEN 'Sábado'
                        WHEN 'Sunday' THEN 'Domingo'
                    END,
                    COUNT(a)
                FROM Avaliacao a
                WHERE a.dataCriacao >= :dataLimite
                GROUP BY trim(to_char(a.dataCriacao, 'Day'))
                """;

        return converterParaMap(entityManager.createQuery(sql, Object[].class)
                .setParameter("dataLimite", dataLimite)
                .getResultList());
    }

    public Double mediaNotaPeriodo(LocalDateTime dataLimite) {
        String jpqlMedia = "SELECT AVG(a.nota) FROM Avaliacao a WHERE a.dataCriacao >= :dataLimite";
        return  (Double) entityManager.createQuery(jpqlMedia)
                .setParameter("dataLimite", dataLimite)
                .getSingleResult();
    }

    private Map<String, Long> converterParaMap(List<Object[]> resultados) {
        return resultados.stream()
                .collect(Collectors.toMap(
                        linha -> String.valueOf(linha[0]).trim(),
                        linha -> ((Number) linha[1]).longValue()
                ));
    }
}
