package Tienda.de.Video.Juegos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.service.VideojuegoService;

@RestController
@RequestMapping("/api/videojuegos")
@CrossOrigin(origins = "*") // Temporal: en el paso de seguridad el CORS pasa a SecurityConfig
public class VideojuegoController {

    private final VideojuegoService videojuegoService;

    public VideojuegoController(VideojuegoService videojuegoService) {
        this.videojuegoService = videojuegoService;
    }

    // Catalogo completo (lo usa la tienda y el panel admin)
    @GetMapping
    public List<Videojuego> listarTodos() {
        return videojuegoService.obtenerTodos();
    }

    // Un juego por id
    @GetMapping("/{id}")
    public ResponseEntity<Videojuego> obtenerPorId(@PathVariable Long id) {
        return videojuegoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear juego (panel admin)
    @PostMapping
    public ResponseEntity<Videojuego> crear(@RequestBody Videojuego videojuego) {
        Videojuego nuevo = videojuegoService.guardar(videojuego);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // Editar juego (panel admin)
    @PutMapping("/{id}")
    public ResponseEntity<Videojuego> actualizar(@PathVariable Long id, @RequestBody Videojuego videojuego) {
        try {
            return ResponseEntity.ok(videojuegoService.actualizar(id, videojuego));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar juego (panel admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        videojuegoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}