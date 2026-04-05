<div align="center">

# 📦 StockFlow Backend

**Sistema backend de Punto de Venta (POS) para gestión de productos, inventario y ventas.**

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io/)

</div>

---

## 🗂️ Tabla de contenidos

- [Descripción](#-descripción)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Endpoints](#-endpoints)
- [Cómo levantar el proyecto](#-cómo-levantar-el-proyecto)
- [Variables de entorno](#-variables-de-entorno)
- [Seguridad](#-seguridad)

---

## 📋 Descripción

StockFlow es una API REST que permite gestionar un punto de venta completo:

- Registro y autenticación de usuarios con JWT
- CRUD de productos con soft delete y búsqueda por nombre, marca, categoría y código de barras
- Gestión de categorías
- Procesamiento de ventas con control de stock automático, métodos de pago y cálculo de vuelto
- Control de roles: `ADMIN` y `USER`
- Documentación interactiva con Swagger UI

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje principal |
| Spring Boot 3.4.1 | Framework base |
| Spring Security + JWT | Autenticación y autorización |
| Spring Data JPA + Hibernate | Persistencia |
| MySQL 8 | Base de datos |
| MapStruct | Mapeo entre entidades y DTOs |
| Lombok | Reducción de boilerplate |
| Bean Validation | Validación de entradas |
| Springdoc OpenAPI | Documentación Swagger |
| Docker + Docker Compose | Containerización |

---

## 🏗️ Arquitectura

```
src/main/java/com/stockflow_backend/
├── configuration/
│   ├── filter/
│   │   └── JwtTokenValidator.java     # Filtro JWT por request
│   ├── SecurityConfig.java            # Configuración de Spring Security
│   └── UserDetail.java                # UserDetailsService personalizado
├── controllers/                       # Endpoints REST
├── dto/
│   ├── request/                       # DTOs de entrada
│   └── response/                      # DTOs de salida
├── entities/                          # Entidades JPA
├── exceptions/                        # Excepciones personalizadas + GlobalExceptionHandler
├── mapper/                            # Interfaces MapStruct
├── repositories/                      # Interfaces JPA Repository
├── services/                          # Lógica de negocio
└── utils/
    └── JwtUtils.java                  # Creación y validación de tokens
```

---

## 📡 Endpoints

### 🔐 Auth — `/auth`

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/register` | Registrar nuevo usuario | ❌ |
| POST | `/auth/login` | Login y obtención de JWT | ❌ |

### 📦 Productos — `/products`

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/products` | Listar productos activos (paginado) | ✅ |
| GET | `/products/{id}` | Obtener producto por ID | ✅ |
| GET | `/products/barcode/{barcode}` | Buscar por código de barras | ✅ |
| GET | `/products/name/{name}` | Buscar por nombre | ✅ |
| GET | `/products/brand/{brand}` | Buscar por marca | ✅ |
| GET | `/products/category/{categoryId}` | Filtrar por categoría | ✅ |
| POST | `/products` | Crear producto | 🔒 ADMIN |
| PUT | `/products/update/{id}` | Actualizar producto | 🔒 ADMIN |
| PATCH | `/products/stock/{id}/{quantity}` | Agregar stock | 🔒 ADMIN |
| PATCH | `/products/updateStatus/{id}` | Actualizar estado | 🔒 ADMIN |

### 🗂️ Categorías — `/categories`

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/categories` | Listar categorías (paginado) | ✅ |
| GET | `/categories/{id}` | Obtener categoría por ID | ✅ |
| POST | `/categories` | Crear categoría | 🔒 ADMIN |
| PUT | `/categories/{id}` | Editar categoría | 🔒 ADMIN |
| DELETE | `/categories/{id}` | Eliminar categoría | 🔒 ADMIN |

### 🧾 Ventas — `/sales`

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | `/sales` | Listar ventas (paginado) | ✅ |
| GET | `/sales/{id}` | Obtener venta por ID | ✅ |
| POST | `/sales` | Crear venta | ✅ |
| PATCH | `/sales/{id}/status/{status}` | Cambiar estado (`COMPLETED` / `CANCELED`) | 🔒 ADMIN |
| PATCH | `/sales/{saleId}/products/{productId}` | Eliminar producto de una venta | 🔒 ADMIN |

> ✅ Requiere token JWT — 🔒 Requiere token JWT con rol ADMIN

---

## 🚀 Cómo levantar el proyecto

### Opción 1: Docker Compose (recomendado)

Solo necesitás tener Docker instalado.

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/stockflow-backend.git
cd stockflow-backend

# Crear el archivo de variables de entorno
cp .env.example .env
# Editá el .env con tus valores

# Levantar los servicios (base de datos + backend)
docker compose up -d
```

La API queda disponible en `http://localhost:8080`  
La documentación Swagger en `http://localhost:8080/swagger-ui/index.html`

---

### Opción 2: Local con Maven

**Requisitos:** Java 17, Maven, MySQL 8 corriendo localmente.

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/stockflow-backend.git
cd stockflow-backend

# Crear la base de datos en MySQL
mysql -u root -p -e "CREATE DATABASE stockflow;"

# Configurar las variables de entorno o editar application.properties
# Ver sección de Variables de entorno

# Compilar y ejecutar
./mvnw spring-boot:run
```

---

## ⚙️ Variables de entorno

Creá un archivo `.env` en la raíz del proyecto basándote en este ejemplo:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/stockflow
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_password

JWT_SECRET_KEY=tu_clave_secreta_larga_y_segura
JWT_USER_GENERATOR=StockflowBackend
```

> ⚠️ **Nunca subas el archivo `.env` a GitHub.** Ya está incluido en el `.gitignore`.

---

## 🔐 Seguridad

La autenticación se maneja con **JWT (JSON Web Tokens)**:

1. El usuario se registra en `/auth/register` y recibe rol `USER` por defecto.
2. Al hacer login en `/auth/login`, recibe un token JWT válido por **6 horas**.
3. Para acceder a endpoints protegidos, incluir el token en el header:

```
Authorization: Bearer <token>
```

Los passwords se almacenan hasheados con **BCrypt**.

Los roles disponibles son:
- `USER` — puede consultar productos, categorías y crear ventas
- `ADMIN` — acceso completo, incluyendo creación, edición y eliminación

---

## 📄 Licencia

Este proyecto es de uso libre para fines educativos y de portfolio.
