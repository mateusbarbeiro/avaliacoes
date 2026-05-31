package org.adjt.entity;

public enum NivelCriticidade {
    CRITICO, URGENTE, NORMAL;

    public static NivelCriticidade getDadoNota(double nota) {
        if (nota > 7)
            return NORMAL;

        if (nota > 5)
            return URGENTE;

        return CRITICO;
    }
}