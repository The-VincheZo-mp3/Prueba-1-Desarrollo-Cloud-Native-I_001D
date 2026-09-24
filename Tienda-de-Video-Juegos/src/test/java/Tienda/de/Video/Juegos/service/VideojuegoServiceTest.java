package Tienda.de.Video.Juegos.service;

import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.repository.VideojuegoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideojuegoServiceTest {

    @Mock
    private VideojuegoRepository videojuegoRepository;

    @InjectMocks
    private VideojuegoService videojuegoService;

    private Videojuego juego;

    @BeforeEach
    void setUp() {
        juego = new Videojuego("Zelda: Tears of the Kingdom", "Aventura", null, 59.99, null, 10, null, null);
        juego.setId(1L);
    }

    @Test
    void obtenerTodos_DebeRetornarListaDeVideojuegos() {
        when(videojuegoRepository.findAll()).thenReturn(Arrays.asList(juego));

        List<Videojuego> resultados = videojuegoService.obtenerTodos();

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals("Zelda: Tears of the Kingdom", resultados.get(0).getTitulo());
        verify(videojuegoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DebeRetornarVideojuego() {
        when(videojuegoRepository.findById(1L)).thenReturn(Optional.of(juego));

        Optional<Videojuego> resultado = videojuegoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(videojuegoRepository, times(1)).findById(1L);
    }

    @Test
    void guardar_DebeRetornarVideojuegoGuardado() {
        when(videojuegoRepository.save(any(Videojuego.class))).thenReturn(juego);

        Videojuego guardado = videojuegoService.guardar(juego);

        assertNotNull(guardado);
        assertEquals("Zelda: Tears of the Kingdom", guardado.getTitulo());
        verify(videojuegoRepository, times(1)).save(juego);
    }

    @Test
    void eliminar_DebeLlamarRepositoryDeleteById() {
        doNothing().when(videojuegoRepository).deleteById(1L);

        videojuegoService.eliminar(1L);

        verify(videojuegoRepository, times(1)).deleteById(1L);
    }
}