package Tienda.de.Video.Juegos.service;

import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.repository.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideojuegoService {

    private final VideojuegoRepository videojuegoRepository;

    @Autowired
    public VideojuegoService(VideojuegoRepository videojuegoRepository) {
        this.videojuegoRepository = videojuegoRepository;
    }

    public List<Videojuego> obtenerTodos() {
        return videojuegoRepository.findAll();
    }

    public Optional<Videojuego> obtenerPorId(Long id) {
        return videojuegoRepository.findById(id);
    }

    public Videojuego guardar(Videojuego videojuego) {
        return videojuegoRepository.save(videojuego);
    }

    // Actualiza TODOS los campos editables (antes solo copiaba titulo, genero, precio y stock)
    public Videojuego actualizar(Long id, Videojuego detalles) {
        return videojuegoRepository.findById(id).map(videojuego -> {
            videojuego.setTitulo(detalles.getTitulo());
            videojuego.setGenero(detalles.getGenero());
            videojuego.setDesarrollador(detalles.getDesarrollador());
            videojuego.setPrecio(detalles.getPrecio());
            videojuego.setDescuento(detalles.getDescuento());
            videojuego.setStock(detalles.getStock());
            videojuego.setDescripcion(detalles.getDescripcion());
            videojuego.setImagenUrl(detalles.getImagenUrl());
            return videojuegoRepository.save(videojuego);
        }).orElseThrow(() -> new RuntimeException("Videojuego no encontrado con id: " + id));
    }

    public void eliminar(Long id) {
        videojuegoRepository.deleteById(id);
    }
}