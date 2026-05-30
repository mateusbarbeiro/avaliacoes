package org.adjt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

import static org.adjt.entity.NivelCriticidade.*;

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

    @JsonIgnore
    public NivelCriticidade getUrgencia() {
        if (nota > 7)
            return NORMAL;

        if (nota > 5)
            return URGENTE;

        return CRITICO;
    }

    @JsonIgnore
    public boolean isDeverAvisar() {
        return getUrgencia() == URGENTE || getUrgencia() == CRITICO;
    }
}