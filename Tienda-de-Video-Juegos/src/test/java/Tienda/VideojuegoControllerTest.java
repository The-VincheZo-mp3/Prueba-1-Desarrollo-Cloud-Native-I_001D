package Tienda;

import Tienda.de.Video.Juegos.controller.VideojuegoController;
import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.service.VideojuegoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideojuegoController.class)
class VideojuegoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideojuegoService videojuegoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Videojuego juego;

    @BeforeEach
    void setUp() {
        juego = new Videojuego("Elden Ring", "RPG", null, 69.99, null, 15, null, null);
        juego.setId(1L);
    }

    @Test
    void listarTodos_DebeRetornarStatus200YLista() throws Exception {
        when(videojuegoService.obtenerTodos()).thenReturn(Arrays.asList(juego));

        mockMvc.perform(get("/api/videojuegos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Elden Ring"))
                .andExpect(jsonPath("$[0].precio").value(69.99));
    }

    @Test
    void obtenerPorId_CuandoExiste_DebeRetornarStatus200() throws Exception {
        when(videojuegoService.obtenerPorId(1L)).thenReturn(Optional.of(juego));

        mockMvc.perform(get("/api/videojuegos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Elden Ring"));
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DebeRetornarStatus404() throws Exception {
        when(videojuegoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/videojuegos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_DebeRetornarStatus201() throws Exception {
        when(videojuegoService.guardar(any(Videojuego.class))).thenReturn(juego);

        mockMvc.perform(post("/api/videojuegos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(juego)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Elden Ring"));
    }

    @Test
    void eliminar_DebeRetornarStatus204() throws Exception {
        mockMvc.perform(delete("/api/videojuegos/1"))
                .andExpect(status().isNoContent());
    }
}