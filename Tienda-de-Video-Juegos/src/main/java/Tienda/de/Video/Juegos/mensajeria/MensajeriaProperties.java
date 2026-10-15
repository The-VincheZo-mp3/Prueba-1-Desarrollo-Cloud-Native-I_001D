package Tienda.de.Video.Juegos.mensajeria;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Valores "mensajeria.*" de application.properties.
 */
@ConfigurationProperties(prefix = "mensajeria")
public record MensajeriaProperties(
        String colaOrdenes,
        String dlqOrdenes,
        String topicoNotificaciones,
        boolean simularFallo,
        int umbralStockBajo) {}
