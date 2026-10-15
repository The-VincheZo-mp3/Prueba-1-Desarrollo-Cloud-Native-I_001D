package Tienda.de.Video.Juegos.mensajeria;

/**
 * Error que NO se arregla reintentando (ej: sin stock, juego inexistente).
 * La orden se manda directo a la DLQ y el mensaje se confirma.
 */
public class ErrorDefinitivo extends RuntimeException {
    public ErrorDefinitivo(String mensaje) { super(mensaje); }
}
