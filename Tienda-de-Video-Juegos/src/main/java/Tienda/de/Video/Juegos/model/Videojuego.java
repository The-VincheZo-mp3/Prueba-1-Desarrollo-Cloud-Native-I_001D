package Tienda.de.Video.Juegos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "videojuegos")
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String genero;
    private String desarrollador;
    private Double precio;
    private Double descuento;
    private Integer stock;

    @Column(length = 1000)
    private String descripcion;

    private String imagenUrl;

    public Videojuego() {}

    public Videojuego(String titulo, String genero, String desarrollador, Double precio, Double descuento, Integer stock, String descripcion, String imagenUrl) {
        this.titulo = titulo;
        this.genero = genero;
        this.desarrollador = desarrollador;
        this.precio = precio;
        this.descuento = descuento;
        this.stock = stock;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getDesarrollador() { return desarrollador; }
    public void setDesarrollador(String desarrollador) { this.desarrollador = desarrollador; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public Double getDescuento() { return descuento; }
    public void setDescuento(Double descuento) { this.descuento = descuento; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}   