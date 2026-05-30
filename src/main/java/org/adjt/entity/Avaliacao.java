package org.adjt.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class Avaliacao extends PanacheEntity {

    public String descricao;
    public Integer nota;
    public LocalDateTime dataCriacao;

    // Construtor vazio obrigatório para o Hibernate
    public Avaliacao() {
        this.dataCriacao = LocalDateTime.now();
    }
}