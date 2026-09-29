package com.caio.agenteagenda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync        // processa mensagens em segundo plano (Etapa 3)
@EnableScheduling   // lembrete diário das 7h (Etapa 5)
public class AgenteAgendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenteAgendaApplication.class, args);
    }
}
