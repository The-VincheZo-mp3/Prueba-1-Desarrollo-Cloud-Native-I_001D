package Tienda.de.Video.Juegos.service;

import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.repository.VideojuegoRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VideojuegoRepositoryTest {

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    @Test
    void guardarYBuscarPorId_DebePersistirEnBaseDeDatos() {
        Videojuego juego = new Videojuego("Super Mario Bros", "Plataformas", null, 49.99, null, 20, null, null);
        Videojuego guardado = videojuegoRepository.save(juego);

        assertNotNull(guardado.getId());

        Optional<Videojuego> encontrado = videojuegoRepository.findById(guardado.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Super Mario Bros", encontrado.get().getTitulo());
    }

    @Test
    void eliminar_DebeRemoverDeBaseDeDatos() {
        Videojuego juego = new Videojuego("Halo Infinite", "FPS", null, 39.99, null, 5, null, null);
        Videojuego guardado = videojuegoRepository.save(juego);

        videojuegoRepository.deleteById(guardado.getId());

        Optional<Videojuego> encontrado = videojuegoRepository.findById(guardado.getId());
        assertFalse(encontrado.isPresent());
    }
}