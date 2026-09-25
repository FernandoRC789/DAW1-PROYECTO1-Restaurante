# AUTH SERVICE

Microservicio encargado de la gestión de usuarios, roles, autenticación y autorización del sistema de restaurante.

Forma parte de la arquitectura de microservicios del proyecto **DAW1-PROYECTO1-Restaurante**.

---

## 1. Responsabilidad

El AUTH SERVICE centraliza las funcionalidades relacionadas con la identidad y seguridad de los usuarios.

Sus principales responsabilidades son:

* Gestión de usuarios.
* Gestión de roles.
* Relación entre usuarios y roles.
* Persistencia de usuarios y roles.
* Registro y consulta de usuarios mediante API REST.
* Autenticación mediante usuario y contraseña.
* Encriptación de contraseñas mediante BCrypt.
* Generación y validación de JWT.
* Autorización mediante roles.
* Protección de endpoints mediante Spring Security.

El AUTH SERVICE no mantiene relaciones JPA con entidades pertenecientes a otros microservicios.

---

## 2. Tecnologías

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* BCrypt
* JWT
* MySQL
* Maven
* Swagger / OpenAPI
* JUnit
* Spring Security Test

---

## 3. Estructura general

```text
auth-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/cibertec/auth_service/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
└── README.md
```

---

## 4. Entidades principales

### User

Representa a los usuarios que pueden autenticarse en el sistema.

Entre sus responsabilidades se encuentran:

* Identificación del usuario.
* Username.
* Contraseña almacenada mediante BCrypt.
* Roles asignados.

### Role

Representa los roles utilizados para controlar el acceso a las funcionalidades del sistema.

La relación entre `User` y `Role` se mantiene dentro del AUTH SERVICE.

No existe una relación JPA directa entre `User` y entidades pertenecientes al ORDER SERVICE u otros microservicios.

---

## 5. Gestión REST de usuarios

La API permite realizar operaciones CRUD sobre los usuarios.

### Puerto

```text
http://localhost:8082/
```

### Endpoint base

```text
/api/usuarios
```

### Listar usuarios

```http
GET /api/usuarios
```

Obtiene la lista de usuarios registrados.

### Obtener usuario por ID

```http
GET /api/usuarios/{id}
```

Obtiene un usuario específico mediante su identificador.

### Crear usuario

```http
POST /api/usuarios
```

Permite registrar un nuevo usuario.

### Actualizar usuario

```http
PUT /api/usuarios/{id}
```

Actualiza la información de un usuario existente.

### Eliminar usuario

```http
DELETE /api/usuarios/{id}
```

Elimina un usuario mediante su identificador.

### Buscar por username

```http
GET /api/usuarios/buscar?username={username}
```

Permite buscar usuarios mediante su username.

### Buscar usuarios por rol

```http
GET /api/usuarios/rol/{rol}
```

Obtiene los usuarios asociados a un determinado rol.

### Contar usuarios por rol

```http
GET /api/usuarios/rol/{rol}/count
```

Obtiene la cantidad de usuarios asociados a un determinado rol.

---

## 6. Autenticación

La autenticación utiliza:

* Spring Security.
* Username y contraseña.
* BCrypt para las contraseñas.
* JWT para representar la autenticación.
* Roles para controlar la autorización.

### Flujo general

```text
Usuario
   │
   ▼
Login
   │
   ▼
AUTH SERVICE
   │
   ├── Verifica username
   ├── Verifica contraseña mediante BCrypt
   └── Genera JWT
          │
          ▼
       Cliente
          │
          ▼
  Solicitud protegida
          │
          ▼
     JWT enviado
          │
          ▼
    Spring Security
          │
       ┌──┴──┐
       │     │
     válido inválido
       │     │
       ▼     ▼
    Acceso  Rechazo
```

---

## 7. JWT

El JWT permite identificar al usuario autenticado y transportar información relacionada con sus roles.

Las solicitudes hacia recursos protegidos deben realizarse utilizando un token válido.

Conceptualmente:

```http
Authorization: Bearer <JWT>
```

El sistema valida el token antes de permitir el acceso a los recursos protegidos.

---

## 8. Roles y autorización

Los roles permiten restringir el acceso a determinadas funcionalidades.

Los roles utilizados por el sistema incluyen:

```text
ADMIN
MESERO
COCINA
CAJERO
```

La autorización se realiza mediante Spring Security.

Un usuario puede acceder a los recursos permitidos para el rol o roles que tenga asociados.

---

## 9. Seguridad de contraseñas

Las contraseñas no se almacenan directamente como texto plano.

AUTH SERVICE utiliza:

```text
BCryptPasswordEncoder
```

para almacenar las contraseñas de forma protegida.

Durante la autenticación, Spring Security verifica la contraseña proporcionada contra el valor almacenado.

---

## 10. Swagger / OpenAPI

La API del AUTH SERVICE cuenta con documentación mediante Swagger / OpenAPI.

Con el servicio ejecutándose, se puede acceder a:

```text
/swagger-ui/index.html
```

y a la especificación OpenAPI mediante:

```text
/v3/api-docs
```

Swagger permite consultar los endpoints disponibles y realizar pruebas sobre la API.

---

## 11. Validaciones realizadas

Como parte de la validación del AUTH SERVICE se verificaron las siguientes funcionalidades:

* [x] `UserRepository`.
* [x] `RoleRepository`.
* [x] Creación de usuarios.
* [x] Relación usuario/rol.
* [x] Login válido.
* [x] Login inválido rechazado.
* [x] Generación de JWT.
* [x] Validación de JWT.
* [x] Acceso mediante JWT válido a recursos protegidos.
* [x] Rechazo de solicitudes sin JWT o con JWT inválido.
* [x] Endpoints CRUD de usuarios.
* [x] BCrypt para contraseñas.
* [x] Autorización mediante roles.
* [x] Documentación Swagger/OpenAPI.
* [x] Pruebas mediante herramientas de desarrollo y cliente frontend.

---

## 12. Pruebas

Se realizaron pruebas relacionadas con:

### Persistencia

* UserRepository.
* RoleRepository.
* Persistencia de usuarios.
* Persistencia de roles.
* Relación entre usuarios y roles.

### API REST

* GET.
* POST.
* PUT.
* DELETE.
* Endpoints adicionales de búsqueda.

### Seguridad

* Login válido.
* Login con credenciales inválidas.
* Generación de JWT.
* Validación de JWT.
* Acceso a endpoints protegidos.
* Rechazo de solicitudes sin autenticación.
* Rechazo de tokens inválidos.
* Autorización según roles.
* Verificación del almacenamiento mediante BCrypt.

---

## 13. Ejecución

Desde la carpeta del AUTH SERVICE:

```bash
mvn spring-boot:run
```

También puede ejecutarse mediante el IDE utilizado para el desarrollo.

El puerto utilizado debe corresponder con la configuración definida en:

```text
src/main/resources/application.properties
```

Una vez iniciado el servicio, verificar que la API y Swagger estén disponibles.

---

## 14. Relación con otros microservicios

AUTH SERVICE es responsable de la identidad y seguridad.

Los demás microservicios no deben crear relaciones JPA directas con las entidades de AUTH.

Por ejemplo, un pedido puede almacenar el identificador del usuario:

```text
waiterId = 15
```

pero ORDER SERVICE no mantiene una relación JPA:

```text
Pedido → User
```

La comunicación entre microservicios se manejará mediante contratos/API y mecanismos definidos por la arquitectura general del proyecto.

---

## 15. Estado del servicio

### Definition of Done — AUTH SERVICE

* [x] Entidades implementadas.
* [x] Repositorios implementados.
* [x] Persistencia validada.
* [x] Gestión REST de usuarios implementada.
* [x] Validaciones implementadas.
* [x] Spring Security configurado.
* [x] BCrypt implementado.
* [x] Autenticación implementada.
* [x] JWT implementado.
* [x] Autorización por roles implementada.
* [x] Pruebas realizadas.
* [x] Swagger/OpenAPI disponible.
* [x] Documentación actualizada.

**Estado: COMPLETADO**
