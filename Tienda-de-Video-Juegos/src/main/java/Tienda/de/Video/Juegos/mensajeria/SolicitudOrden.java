package Tienda.de.Video.Juegos.mensajeria;

/**
 * Lo que envia el frontend de la tienda al apretar "Comprar".
 */
public record SolicitudOrden(Long videojuegoId, Integer cantidad, String cliente) {}
