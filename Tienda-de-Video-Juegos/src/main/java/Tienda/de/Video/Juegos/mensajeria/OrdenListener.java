package Tienda.de.Video.Juegos.mensajeria;

import Tienda.de.Video.Juegos.model.Videojuego;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CONSUMIDOR: escucha la cola SQS y procesa cada orden.
 *  - Exito            -> descuenta stock + correo de confirmacion (SNS) + se confirma el mensaje.
 *  - ErrorDefinitivo  -> se envia a la DLQ + correo de rechazo + se confirma el mensaje.
 *  - ErrorTransitorio -> NO se captura: el mensaje no se confirma y SQS lo reintenta.
 *                        Al 3er intento la redrive policy de la cola lo mueve a la DLQ.
 */
@Component
public class OrdenListener {

    private static final Logger log = LoggerFactory.getLogger(OrdenListener.class);

    private final ProcesadorOrden procesador;
    private final Notificador notificador;
    private final SqsTemplate sqsTemplate;
    private final MensajeriaProperties props;

    // Idempotencia: SQS estandar puede entregar un mensaje mas de una vez
    private final Set<String> procesadas = ConcurrentHashMap.newKeySet();

    public OrdenListener(ProcesadorOrden procesador, Notificador notificador,
                         SqsTemplate sqsTemplate, MensajeriaProperties props) {
        this.procesador = procesador;
        this.notificador = notificador;
        this.sqsTemplate = sqsTemplate;
        this.props = props;
    }

    @SqsListener("${mensajeria.cola-ordenes}")
    public void escuchar(Orden orden) {
        log.info("Recibida orden {} (juego {}, cantidad {}, cliente {})",
                orden.id(), orden.videojuegoId(), orden.cantidad(), orden.cliente());

        if (procesadas.contains(orden.id())) {
            log.warn("Orden {} duplicada, se ignora", orden.id());
            return;
        }

        try {
            Videojuego juego = procesador.procesar(orden);
            procesadas.add(orden.id()); // se marca SOLO cuando termino bien
            notificador.compraConfirmada(orden, juego);
        } catch (ErrorDefinitivo e) {
            log.error("Orden {} rechazada: {}", orden.id(), e.getMessage());
            procesadas.add(orden.id());
            sqsTemplate.send(props.dlqOrdenes(), orden);
            notificador.ordenRechazada(orden, e.getMessage());
        }
        // ErrorTransitorio no se captura a proposito (ver comentario de la clase)
    }
}
