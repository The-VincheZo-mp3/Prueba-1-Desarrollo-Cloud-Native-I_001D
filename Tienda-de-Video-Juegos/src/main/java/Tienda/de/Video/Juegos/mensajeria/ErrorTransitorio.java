package Tienda.de.Video.Juegos.mensajeria;

/**
 * Error temporal (ej: servicio externo caido). El mensaje NO se confirma,
 * SQS lo vuelve a entregar y, tras 3 intentos, la redrive policy lo manda a la DLQ.
 */
public class ErrorTransitorio extends RuntimeException {
    public ErrorTransitorio(String mensaje) { super(mensaje); }
}
