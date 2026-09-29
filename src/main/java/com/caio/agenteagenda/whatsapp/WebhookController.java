package com.caio.agenteagenda.whatsapp;

import com.caio.agenteagenda.config.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final AppProperties props;
    private final SignatureValidator signatureValidator;
    private final ObjectMapper mapper;

    public WebhookController(AppProperties props, SignatureValidator signatureValidator, ObjectMapper mapper) {
        this.props = props;
        this.signatureValidator = signatureValidator;
        this.mapper = mapper;
    }

    /** A Meta chama uma vez ao cadastrar o webhook. */
    @GetMapping
    public ResponseEntity<String> verify(@RequestParam(name = "hub.mode", required = false) String mode,
                                         @RequestParam(name = "hub.verify_token", required = false) String token,
                                         @RequestParam(name = "hub.challenge", required = false) String challenge) {
        String expected = props.whatsapp().verifyToken();
        if ("subscribe".equals(mode) && expected != null && !expected.isBlank() && expected.equals(token)) {
            log.info("✅ Webhook verificado.");
            return ResponseEntity.ok(challenge);
        }
        log.warn("❌ Verificação falhou (token incorreto).");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /** A Meta chama a cada mensagem recebida. */
    @PostMapping
    public ResponseEntity<Void> receive(@RequestHeader(name = "X-Hub-Signature-256", required = false) String signature,
                                        @RequestBody byte[] body) {
        if (!signatureValidator.isValid(body, signature)) {
            log.warn("❌ Requisição rejeitada: assinatura inválida.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            JsonNode root = mapper.readTree(body);
            for (IncomingMessage msg : WebhookParser.parse(root)) {
                log.info("📩 De: {} ({}) | tipo: {} | texto: {} | botão: {}",
                        msg.senderName(), msg.from(), msg.type(), msg.text(), msg.buttonId());
                // Etapa 3: aqui vamos chamar o EventService
            }
        } catch (IOException e) {
            log.error("JSON inválido no webhook", e);
        }
        return ResponseEntity.ok().build(); // sempre 200, senão a Meta reenvia
    }
}
