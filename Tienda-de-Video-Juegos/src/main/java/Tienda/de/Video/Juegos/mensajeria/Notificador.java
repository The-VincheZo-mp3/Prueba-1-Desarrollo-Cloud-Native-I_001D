package Tienda.de.Video.Juegos.mensajeria;

import Tienda.de.Video.Juegos.model.Videojuego;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;

/**
 * Publica avisos en el topico SNS (que los reenvia por correo a los suscriptores).
 */
@Component
public class Notificador {

    private static final Logger log = LoggerFactory.getLogger(Notificador.class);

    private final SnsClient snsClient;
    private final MensajeriaProperties props;

    public Notificador(SnsClient snsClient, MensajeriaProperties props) {
        this.snsClient = snsClient;
        this.props = props;
    }

    public void compraConfirmada(Orden orden, Videojuego juego) {
        double precio = juego.getPrecio() == null ? 0 : juego.getPrecio();
        double descuento = juego.getDescuento() == null ? 0 : juego.getDescuento();
        double total = precio * (1 - descuento / 100) * orden.cantidad();

        publicar("GamingStore - Compra confirmada",
                "Hola " + orden.cliente() + ",\n\n"
                + "Tu compra fue procesada correctamente.\n"
                + "Juego: " + juego.getTitulo() + "\n"
                + "Cantidad: " + orden.cantidad() + "\n"
                + "Total: $" + Math.round(total) + "\n"
                + "N de orden: " + orden.id() + "\n\n"
                + "Gracias por comprar en GamingStore.");

        int umbral = props.umbralStockBajo() > 0 ? props.umbralStockBajo() : 3;
        if (juego.getStock() != null && juego.getStock() <= umbral) {
            publicar("GamingStore - Stock bajo",
                    "Atencion personal: '" + juego.getTitulo() + "' quedo con " + juego.getStock() + " unidades.");
        }
    }

    public void ordenRechazada(Orden orden, String motivo) {
        publicar("GamingStore - Orden rechazada",
                "La orden " + orden.id() + " de " + orden.cliente() + " no se pudo procesar.\n"
                + "Motivo: " + motivo + "\n"
                + "La orden quedo en la cola de mensajes fallidos (DLQ) para revision.");
    }

    // Si SNS falla no se cae la orden: solo se registra el error
    private void publicar(String asunto, String mensaje) {
        try {
            snsClient.publish(r -> r.topicArn(props.topicoNotificaciones()).subject(asunto).message(mensaje));
            log.info("Notificacion SNS enviada: {}", asunto);
        } catch (Exception e) {
            log.warn("No se pudo publicar en SNS ({}): {}", asunto, e.getMessage());
        }
    }
}
