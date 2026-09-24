package Tienda.de.Video.Juegos.DataLoader;

import Tienda.de.Video.Juegos.model.Videojuego;
import Tienda.de.Video.Juegos.repository.VideojuegoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(VideojuegoRepository repository) {
        return args -> {
            repository.deleteAll();

            Faker faker = new Faker();
            Random random = new Random();

            // URLs directas desde CDN pública e ilimitada de IGDB (Twitch)
            List<JuegoPreset> juegosBase = List.of(
                new JuegoPreset(
                    "Marvel's Spider-Man 2", 
                    "Acción / Aventuras", 
                    "Insomniac Games", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co708i.jpg"
                ),
                new JuegoPreset(
                    "The Legend of Zelda: Tears of the Kingdom", 
                    "Aventura", 
                    "Nintendo", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co5vmg.jpg"
                ),
                new JuegoPreset(
                    "Elden Ring", 
                    "RPG / Soulslike", 
                    "FromSoftware", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co4ni8.jpg"
                ),
                new JuegoPreset(
                    "God of War Ragnarök", 
                    "Acción", 
                    "Santa Monica Studio", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co5s5v.jpg"
                ),
                new JuegoPreset(
                    "Cyberpunk 2077: Phantom Liberty", 
                    "RPG / Ciberpunk", 
                    "CD Projekt Red", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co6m93.jpg"
                ),
                new JuegoPreset(
                    "Grand Theft Auto V", 
                    "Mundo Abierto", 
                    "Rockstar Games", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co1tnw.jpg"
                ),
                new JuegoPreset(
                    "Red Dead Redemption 2", 
                    "Mundo Abierto", 
                    "Rockstar Games", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co1q1f.jpg"
                ),
                new JuegoPreset(
                    "Final Fantasy XVI", 
                    "JRPG", 
                    "Square Enix", 
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co5whu.jpg"
                )
            );

            // Cargar catálogo principal con imágenes funcionales de IGDB
            for (JuegoPreset preset : juegosBase) {
                Videojuego juego = new Videojuego();
                juego.setTitulo(preset.titulo);
                juego.setGenero(preset.genero);
                juego.setDesarrollador(preset.desarrollador);
                juego.setPrecio(Math.round((29990.0 + random.nextDouble() * 40000.0) / 100.0) * 100.0);
                juego.setDescuento(random.nextBoolean() ? 15.0 : 0.0);
                juego.setStock(faker.number().numberBetween(5, 30));
                juego.setDescripcion("Disponible para entrega inmediata. Producto oficial de " + preset.desarrollador + ".");
                juego.setImagenUrl(preset.imagenUrl);
                repository.save(juego);
            }
        };
    }

    private static class JuegoPreset {
        String titulo;
        String genero;
        String desarrollador;
        String imagenUrl;

        JuegoPreset(String titulo, String genero, String desarrollador, String imagenUrl) {
            this.titulo = titulo;
            this.genero = genero;
            this.desarrollador = desarrollador;
            this.imagenUrl = imagenUrl;
        }
    }
}