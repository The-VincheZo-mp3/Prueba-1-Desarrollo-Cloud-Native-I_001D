package Tienda.de.Video.Juegos.mensajeria;

import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.repository.VideojuegoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica de negocio de una orden: validar y descontar stock.
 */
@Component
public class ProcesadorOrden {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorOrden.class);

    private final VideojuegoRepository repository;
    private final MensajeriaProperties props;

    public ProcesadorOrden(VideojuegoRepository repository, MensajeriaProperties props) {
        this.repository = repository;
        this.props = props;
    }

    @Transactional
    public Videojuego procesar(Orden orden) {
        if (props.simularFallo()) {
            // Para la demo: muestra los reintentos de SQS y el paso a la DLQ
            throw new ErrorTransitorio("Servicio de pagos no disponible (simulado)");
        }
        if (orden.cantidad() == null || orden.cantidad() <= 0) {
            throw new ErrorDefinitivo("Cantidad invalida en la orden " + orden.id());
        }

        Videojuego juego = repository.findById(orden.videojuegoId())
                .orElseThrow(() -> new ErrorDefinitivo("El videojuego " + orden.videojuegoId() + " no existe"));

        int stock = juego.getStock() == null ? 0 : juego.getStock();
        if (stock < orden.cantidad()) {
            throw new ErrorDefinitivo("Sin stock suficiente de '" + juego.getTitulo() + "' (quedan " + stock + ")");
        }

        juego.setStock(stock - orden.cantidad());
        Videojuego actualizado = repository.save(juego);
        log.info("Orden {} procesada: '{}' stock {} -> {}", orden.id(), juego.getTitulo(), stock, actualizado.getStock());
        return actualizado;
    }
}
