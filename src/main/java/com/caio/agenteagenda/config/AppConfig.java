package com.caio.agenteagenda.config;

import com.caio.agenteagenda.whatsapp.SignatureValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public SignatureValidator signatureValidator(AppProperties props) {
        return new SignatureValidator(props.whatsapp().appSecret());
    }
}
