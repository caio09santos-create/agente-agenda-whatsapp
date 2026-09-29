package com.caio.agenteagenda.whatsapp;

import java.time.Instant;

/** Mensagem recebida pelo webhook, já simplificada. */
public record IncomingMessage(
        String id,          // ID da mensagem (evita processar duas vezes)
        String from,        // número de quem enviou, só dígitos
        String senderName,  // nome do perfil
        Type type,
        String text,        // texto da mensagem ou título do botão
        String buttonId,    // ID do botão clicado
        Instant timestamp) {

    public enum Type { TEXT, BUTTON_REPLY, OTHER }
}
