package org.adjt.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.adjt.entity.Avaliacao;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class AvisoUrgenciaService {

    @Inject
    MailSender mailSender;

    @ConfigProperty(name = "admin.email")
    String adminEmailNotificado;

    public void notificarAvisoUrgencia(String mensagemFila) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Avaliacao avaliacao = mapper.readValue(mensagemFila, Avaliacao.class);

        String corpoEmail = String.format(
                """
                        Olá Administrador,
                        
                        Uma nova avaliação crítica foi recebida.
                        Descrição: %s
                        Nota: %d
                        Data: %s
                        Urgência: %s
                        
                        Atenciosamente,
                        Equipe""",
                avaliacao.descricao, avaliacao.nota,
                avaliacao.dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")),
                avaliacao.getUrgencia().toString()
        );

        mailSender.sendEmail(adminEmailNotificado, "ALERTA: Avaliação Crítica", corpoEmail);
    }
}
