package com.caio.agenteagenda.whatsapp;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Converte o JSON da Meta (entry[].changes[].value.messages[]) em IncomingMessage. */
public final class WebhookParser {

    private WebhookParser() {
    }

    public static List<IncomingMessage> parse(JsonNode root) {
        List<IncomingMessage> result = new ArrayList<>();

        for (JsonNode entry : root.path("entry")) {
            for (JsonNode change : entry.path("changes")) {
                if (!"messages".equals(change.path("field").asText())) {
                    continue;
                }
                JsonNode value = change.path("value");

                Map<String, String> names = new HashMap<>();
                for (JsonNode contact : value.path("contacts")) {
                    names.put(contact.path("wa_id").asText(), contact.path("profile").path("name").asText(null));
                }

                // "statuses" (entregue/lido) não entram aqui, só "messages"
                for (JsonNode m : value.path("messages")) {
                    result.add(toMessage(m, names));
                }
            }
        }
        return result;
    }

    private static IncomingMessage toMessage(JsonNode m, Map<String, String> names) {
        String id = m.path("id").asText();
        String from = m.path("from").asText();
        String name = names.get(from);
        Instant ts = m.hasNonNull("timestamp")
                ? Instant.ofEpochSecond(m.path("timestamp").asLong())
                : Instant.now();

        return switch (m.path("type").asText()) {
            case "text" -> new IncomingMessage(id, from, name, IncomingMessage.Type.TEXT,
                    m.path("text").path("body").asText(), null, ts);
            case "interactive" -> {
                JsonNode reply = m.path("interactive").path("button_reply");
                yield reply.isMissingNode()
                        ? new IncomingMessage(id, from, name, IncomingMessage.Type.OTHER, null, null, ts)
                        : new IncomingMessage(id, from, name, IncomingMessage.Type.BUTTON_REPLY,
                        reply.path("title").asText(), reply.path("id").asText(), ts);
            }
            default -> new IncomingMessage(id, from, name, IncomingMessage.Type.OTHER, null, null, ts);
        };
    }
}
