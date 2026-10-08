📦 StockFlow Backend

Sistema backend de Punto de Venta (POS) para gestión de productos, inventario, ventas y generación de comprobantes.

---

## 🗂️ Tabla de contenidos

- [Descripción](#-descripción)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Endpoints y Seguridad](#-endpoints-y-seguridad)
- [Cómo levantar el proyecto](#-cómo-levantar-el-proyecto)
- [Variables de entorno](#-variables-de-entorno)
- [Seguridad y Roles](#-seguridad-y-roles)

---

## 📋 Descripción

**StockFlow** es una API REST diseñada para gestionar integralmente un punto de venta. Permite el control de stock en tiempo real, administración de productos y categorías, procesamiento de ventas y generación de reportes.

**Características principales:**
- 🔐 Registro y autenticación de usuarios mediante **JWT**.
- 📦 CRUD completo de productos con búsquedas dinámicas (por nombre, marca, categoría, código de barras) y alertas de bajo stock.
- 🗂️ Gestión de categorías para organizar el inventario.
- 🛒 Procesamiento de ventas con descuento de stock automático y gestión de estados (COMPLETED, CANCELED).
- 📄 Generación automática de comprobantes de venta en PDF.
- 🛡️ Control estricto de accesos basado en roles (`USER` y `ADMIN`).
- 📖 Documentación interactiva e integrada con Swagger UI.

---

## 🛠️ Tecnologías

| Tecnología | Uso en el Proyecto |
| :--- | :--- |
| **Java 17** | Lenguaje principal del backend |
| **Spring Boot 3** | Framework base (Web, Security, Data JPA) |
| **Spring Security + JWT** | Autenticación stateless y autorización por roles |
| **Hibernate / Spring Data JPA** | Capa de persistencia y abstracción de BD |
| **MySQL 8** | Base de datos relacional |
| **MapStruct** | Mapeo eficiente entre Entidades y DTOs |
| **Lombok** | Reducción de código repetitivo (getters, setters, builders) |
| **iTextPDF** | Generación de comprobantes y recibos en formato PDF |
| **Springdoc OpenAPI** | Documentación automática (Swagger) |
| **Docker & Docker Compose** | Containerización y orquestación del entorno |

---

## 🏗️ Arquitectura del Proyecto

```text
src/main/java/com/stockflow_backend/
├── configuration/
│   ├── filter/
│   │   └── JwtTokenValidator.java     # Filtro de validación JWT por cada request
│   ├── DataInitializer.java           # Inicialización automática del usuario ADMIN
│   └── SecurityConfig.java            # Configuración principal de Spring Security
├── controllers/                       # Controladores REST (Auth, Category, Pdf, Product, Sale)
├── dto/
│   ├── request/                       # DTOs de entrada (Payloads)
│   └── response/                      # DTOs de salida
├── entities/                          # Modelos JPA (Product, Category, Sale, UserEntity, etc.)
├── exceptions/                        # Manejo de errores (GlobalExceptionHandler)
├── mapper/                            # Interfaces MapStruct para conversión DTO <-> Entity
├── repositories/                      # Interfaces Spring Data JPA
├── services/                          # Lógica de negocio core
└── utils/
    └── JwtUtils.java                  # Generación, firma y extracción de claims JWT
📡 Endpoints y SeguridadEl sistema utiliza anotaciones y rutas protegidas definidas en SecurityConfig.Simbología:🟢 Público (No requiere token)🔵 Requiere Token (Rol USER o ADMIN)🔴 Requiere Token (Solo rol ADMIN)🔐 Autenticación (/auth)MétodoEndpointDescripciónAccesoPOST/auth/loginIniciar sesión y obtener token JWT🟢 PúblicoPOST/auth/registerRegistrar un nuevo operador🟢 PúblicoDELETE/auth/{id}Eliminar un usuario🔴 ADMIN📦 Productos (/products)MétodoEndpointDescripciónAccesoGET/productsListar productos activos (paginado)🟢 PúblicoGET/products/{id}Obtener detalles de un producto🟢 PúblicoPOST/productsCrear un nuevo producto🔴 ADMINPUT/products/update/{id}Actualizar datos completos🔴 ADMINPATCH/products/stock/{id}/{qty}Agregar o modificar stock🔴 ADMINPATCH/products/discount/...Aplicar descuentos por producto/marca/categoría🔴 ADMINPATCH/products/updateStatus/{id}Habilitar/Deshabilitar producto (Soft Delete)🔴 ADMIN🗂️ Categorías (/categories)MétodoEndpointDescripciónAccesoGET/categoriesListar categorías🔵 USER / ADMINPOST/categoriesCrear categoría🔴 ADMINPUT/categories/{id}Editar categoría🔴 ADMINDELETE/categories/{id}Eliminar categoría🔴 ADMIN🧾 Ventas (/sales)MétodoEndpointDescripciónAccesoPOST/salesRegistrar una nueva venta (descuenta stock)🔵 USER / ADMINGET/salesVer historial de ventas🔴 ADMINGET/sales/{id}Detalle de una venta específica🔴 ADMINPATCH/sales/{id}/status/...Cambiar estado de venta (ej: CANCELED)🔴 ADMIN📄 Comprobantes (/pdf)MétodoEndpointDescripciónAccesoGET/pdf/{saleId}Generar y descargar recibo PDF de la venta🔴 ADMIN🚀 Cómo levantar el proyectoOpción 1: Docker Compose (Recomendado)Requiere tener Docker y Docker Compose instalados. La imagen oficial se descarga automáticamente de Docker Hub.Clonar el repositorio:Bashgit clone [https://github.com/lucascabj4710/stockflow-backend.git](https://github.com/lucascabj4710/stockflow-backend.git)
cd stockflow-backend
Crear el archivo de variables de entorno:Bashcp .env.example .env
# Editá el archivo .env con tus credenciales (ver sección Variables de Entorno)
Levantar los contenedores:Bashdocker compose up -d
El backend estará expuesto en http://localhost:8080 y MySQL en el puerto 3307.Documentación Swagger:Ingresá a http://localhost:8080/swagger-ui/index.htmlOpción 2: Local con Maven (Desarrollo)Requiere Java 17, Maven y una instancia de MySQL 8.Preparar base de datos:SQLCREATE DATABASE stockflow;
Construir y ejecutar:Bash./mvnw clean package -DskipTests
java -jar target/stockflow-backend-0.0.1-SNAPSHOT.jar
⚙️ Variables de entornoCreá un archivo .env en la raíz del proyecto. Este archivo nunca debe subirse al repositorio (está en .gitignore).Fragmento de código# Base de Datos
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/stockflow?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=stockflow_user
SPRING_DATASOURCE_PASSWORD=tu_password_seguro
SPRING_DATASOURCE_DB=stockflow

# JSON Web Token (JWT)
JWT_SECRET_KEY=clave_secreta_super_larga_y_segura_para_firmar_los_tokens_hs256
JWT_USER_GENERATOR=StockFlowAPI

# Credenciales del Primer Admin (Se autogenera al arrancar si no existe)
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin1234
🔐 Seguridad y RolesLa plataforma es stateless y se autentica vía Bearer Token en los headers HTTP:Authorization: Bearer <tu_token_jwt>Flujo de Roles:Al levantar la base de datos por primera vez, DataInitializer lee las credenciales del .env (ADMIN_USERNAME y ADMIN_PASSWORD) y crea un superusuario con rol ADMIN.Los nuevos usuarios registrados a través de /auth/register reciben el rol USER de manera predeterminada.Un operador USER puede consultar stock y cargar ventas en la caja, pero no puede alterar inventario, crear productos, ni visualizar estadísticas de ventas pasadas.Las contraseñas se almacenan de forma segura utilizando el algoritmo BCrypt.
