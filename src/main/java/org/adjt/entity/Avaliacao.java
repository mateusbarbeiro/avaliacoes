package org.adjt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class Avaliacao extends PanacheEntity {

    public String descricao;
    public Integer nota;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime dataCriacao;

    public Avaliacao() {
    }

    public Avaliacao(String descricao, Integer nota) {
        this.descricao = descricao;
        this.nota = nota;
    }

    @PrePersist
    protected void onCreate() {
        if (this.dataCriacao == null)
            this.dataCriacao = LocalDateTime.now();
    }
}