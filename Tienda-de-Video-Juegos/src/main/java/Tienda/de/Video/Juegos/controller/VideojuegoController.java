package Tienda.de.Video.Juegos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.service.VideojuegoService;

@RestController
@RequestMapping("/api/videojuegos")
@CrossOrigin(origins = "*") // <-- IMPORTANTE: Permite peticiones desde React
public class VideojuegoController {

    private final VideojuegoService videojuegoService;

    public VideojuegoController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    @GetMapping
    public List<Videojuego> obtenerTodos() {
        List<Videojuego> videojuegos = new java.util.ArrayList<>();
        for (Object videojuego : videojuegoService.obtenerTodos()) {
            videojuegos.add((Videojuego) videojuego);
        }
        return videojuegos;
    }

    // Mantienes todos tus métodos actuales sin renombrar nada
}