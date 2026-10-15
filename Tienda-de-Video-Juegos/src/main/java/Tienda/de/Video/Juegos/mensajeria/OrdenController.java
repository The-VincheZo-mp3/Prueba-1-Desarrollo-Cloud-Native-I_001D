package Tienda.de.Video.Juegos.mensajeria;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * PRODUCTOR: recibe la compra y la deja en la cola SQS.
 * Responde al tiro (202 Accepted); el procesamiento ocurre en segundo plano en OrdenListener.
 */
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private static final Logger log = LoggerFactory.getLogger(OrdenController.class);

    private final SqsTemplate sqsTemplate;
    private final MensajeriaProperties props;

    public OrdenController(SqsTemplate sqsTemplate, MensajeriaProperties props) {
        this.sqsTemplate = sqsTemplate;
        this.props = props;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody SolicitudOrden solicitud,
                                                     @AuthenticationPrincipal Jwt jwt) {
        if (solicitud.videojuegoId() == null || solicitud.cantidad() == null || solicitud.cantidad() < 1) {
            return ResponseEntity.badRequest().body(Map.of("error", "videojuegoId y cantidad (>= 1) son obligatorios"));
        }

        String cliente = solicitud.cliente() != null && !solicitud.cliente().isBlank()
                ? solicitud.cliente()
                : (jwt != null ? jwt.getClaimAsString("username") : "desconocido");

        Orden orden = new Orden(UUID.randomUUID().toString(), solicitud.videojuegoId(), solicitud.cantidad(), cliente);

        sqsTemplate.send(props.colaOrdenes(), orden);
        log.info("Orden {} enviada a la cola {} (juego {}, cantidad {}, cliente {})",
                orden.id(), props.colaOrdenes(), orden.videojuegoId(), orden.cantidad(), cliente);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "ordenId", orden.id(),
                "estado", "EN_COLA",
                "mensaje", "Orden recibida. Te avisaremos por correo cuando se procese."));
    }
}
