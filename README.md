# 🎮 Tienda de Videojuegos - API Backend

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Maven](https://img.shields.io/badge/Maven-3.x-red?style=for-the-badge&logo=apachemaven)

API RESTful desarrollada con **Spring Boot** para la gestión integral de un inventario de videojuegos, catálogo de productos, procesamiento de ventas y administración de usuarios. Diseñada bajo una arquitectura en capas escalable y principios Cloud Native.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.x
* **Capa de Datos:** Spring Data JPA / Hibernate
* **Base de Datos:** MySQL
* **Seguridad:** Spring Security
* **Gestor de Dependencias:** Apache Maven
* **Herramientas de Desarrollo:** Lombok, Spring Boot DevTools

---

## 🏗️ Arquitectura del Proyecto

El proyecto está organizado en una arquitectura por capas bien definida para garantizar la separación de responsabilidades:

```text
src/main/java/com/tienda/
├── controller/   # Endpoints REST y manejo de peticiones HTTP
├── dto/          # Data Transfer Objects (Request/Response)
├── exception/    # Manejo global de excepciones (ControllerAdvice)
├── model/        # Mapeo de entidades JPA para la base de datos
├── repository/   # Interfaces JpaRepository para consultas a la BD
└── service/      # Lógica de negocio y procesamiento de datos
```
---
🚀 Requisitos Previos
Asegúrate de contar con lo siguiente instalado en tu entorno local:

JDK 17 o superior.
Apache Maven 3.8+
MySQL Server 8.0
Cliente HTTP (Postman, Insomnia o Thunder Client).

🚀 Requisitos Previos
Asegúrate de contar con lo siguiente instalado en tu entorno local:

JDK 17 o superior.

Apache Maven 3.8+

MySQL Server 8.0

Cliente HTTP (Postman, Insomnia o Thunder Client).

⚙️ Configuración e Instalación
1. Clonar el repositorio:
git clone [https://github.com/The-VincheZo-mp3/Prueba-1-Desarrollo-Cloud-Native-I_001D.git](https://github.com/The-VincheZo-mp3/Prueba-1-Desarrollo-Cloud-Native-I_001D.git)
cd "Tienda de Video Juegos - Backend"

Configurar la Base de Datos:
Asegúrate de crear la base de datos en tu servidor local MySQL:

SQL
CREATE DATABASE tienda_videojuegos;
Ajustar credenciales de conexión:
Revisa el archivo src/main/resources/application.properties y coloca tus credenciales:

Properties
spring.datasource.url=jdbc:mysql://localhost:3306/tienda_videojuegos?useSSL=false&serverTimezone=UTC
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
Compilar y compilar las dependencias:

Bash
mvn clean install
Iniciar la aplicación:


Bash
mvn spring-boot:run
La API estará disponible en http://localhost:8080.


📌 Endpoints Principales
🎮 Videojuegos (/api/videojuegos)
GET /api/videojuegos: Obtener todos los videojuegos disponibles.
GET /api/videojuegos/{id}: Obtener detalle de un videojuego por ID.
POST /api/videojuegos: Registrar un nuevo videojuego en el inventario.
PUT /api/videojuegos/{id}: Actualizar la información de un videojuego.
DELETE /api/videojuegos/{id}: Eliminar un producto.


🏷️ Categorías (/api/categorias)
GET /api/categorias: Listar categorías disponibles (Acción, RPG, Aventura, etc.).

POST /api/categorias: Crear una nueva categoría.


🧪 Pruebas Unitarias
Para ejecutar la suite de pruebas unitarias creadas para los servicios y controladores:

Bash
mvn test
🧑‍💻 Autor
The-VincheZo-mp3 - Desarrollo Backend y Cloud Native


---

### Pasos para guardar el nuevo README y enviarlo a GitHub:

1. Reemplaza el texto en tu archivo `README.md`.
2. Ejecuta este comando en tu **Git Bash**:

bash
git add README.md && git commit -m "docs: actualizacion completa de documentacion en README.md" && git push origin main

