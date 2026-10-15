package Tienda.de.Video.Juegos.mensajeria;

/**
 * Mensaje que viaja por la cola SQS: una compra de un cliente.
 */
public record Orden(String id, Long videojuegoId, Integer cantidad, String cliente) {}
