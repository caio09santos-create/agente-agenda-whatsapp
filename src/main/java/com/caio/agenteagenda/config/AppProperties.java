package com.caio.agenteagenda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String timezone,
        WhatsApp whatsapp,
        Anthropic anthropic,
        Google google,
        Reminder reminder) {

    public record WhatsApp(String verifyToken, String appSecret, String accessToken, String phoneNumberId,
                           String ownerPhone, String apiVersion, String reminderTemplate,
                           String templateLanguage) {
    }

    public record Anthropic(String apiKey, String model) {
    }

    public record Google(String credentialsPath, String tokensDir, String calendarId, int oauthPort) {
    }

    public record Reminder(int hour) {
    }

    public ZoneId zoneId() {
        return ZoneId.of(timezone);
    }
}