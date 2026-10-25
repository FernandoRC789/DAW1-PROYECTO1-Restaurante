# 🍽️ Sistema Web para Gestión de Restaurante - Arquitectura de Microservicios

[![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=spring)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-17+-DD0031?style=for-the-badge&logo=angular)](https://angular.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Events-FF6600?style=for-the-badge&logo=rabbitmq)](https://www.rabbitmq.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Orchestration-326CE5?style=for-the-badge&logo=kubernetes)](https://kubernetes.io/)

---

## 📌 1. Descripción del Proyecto
Plataforma Full Stack empresarial desarrollada para la gestión integral de un restaurante. El sistema implementa una **arquitectura moderna de microservicios orientada a eventos**, separando responsabilidades operativas en servicios independientes e interconectados mediante comunicación sincrónica (REST) y asincrónica (RabbitMQ). El proyecto está diseñado para garantizar alta escalabilidad, desacoplamiento y tolerancia a fallos.

---

## ⚠️ 2. Problema de Negocio y Solución Propuesta

### El Problema
Los restaurantes tradicionales y sistemas monolíticos sufren de cuellos de botella operativos:
- El procesamiento masivo de pedidos bloquea las operaciones de caja y facturación.
- La caída de un solo módulo (ej. catálogo o pagos) colapsa todo el sistema.
- Las bases de datos centralizadas generan altos niveles de contención y bloqueos concurrentes.
- Dificultad para escalar componentes individuales ante alta demanda (horas punta).

### La Solución Propuesta
Se implementó un ecosistema de **microservicios distribuidos**:
- **Desacoplamiento Operativo:** Cada dominio (Auth, Catálogo, Pedidos, Operaciones, Clientes y Facturación) opera con su propia base de datos MySQL independiente.
- **Resiliencia Asincrónica:** Uso de RabbitMQ (`restaurante.topic`) para eventos en segundo plano (notificaciones, confirmaciones de pago y cocina), evitando bloqueos HTTP.
- **Enrutamiento Inteligente y Descubrimiento:** Integración con Netflix Eureka y Spring Cloud Gateway para centralizar la seguridad y el flujo de peticiones.

---

## 🏛️ 3. Arquitectura del Sistema y Diagrama Conceptual

```text
                                [ CLIENTE / NAVEGADOR ]
                                          │
                                          ▼
                         [ Frontend Angular (Nginx) ]
                                          │  (HTTP / API REST)
                                          ▼
                      [ API Gateway (Spring Cloud - Puerto 8081) ]
                                          │
            ┌─────────────────────────────┼─────────────────────────────┐
            ▼                             ▼                             ▼
   [ Auth Service ]             [ Order Service ]             [ Catalog Service ]
   (Puerto 8082)                (Puerto 8084)                 (Puerto 8083)
            │                             │                             │
            └─────────────────────────────┼─────────────────────────────┘
                                          │ (Eventos asíncronos)
                                          ▼
                       [ RabbitMQ Message Broker (Exchange: restaurante.topic) ]
                                          │
            ┌─────────────────────────────┼─────────────────────────────┐
            ▼                             ▼                             ▼
   [ Billing Service ]        [ Operations Service ]        [ Customer Service ]
   (Puerto 8085)                (Puerto 8087)                 (Puerto 8086)
            │                             │                             │
            └─────────────────────────────┴─────────────────────────────┘
                                          │
                                          ▼
                         [ Servidor de Descubrimiento Eureka ]
                                     (Puerto 8761)
```

---

## 🛠️ 4. Tecnologías Utilizadas

### 🔧 Back-End e Infraestructura
* **Java 21** & **Spring Boot 3.x** (Desarrollo de microservicios robustos)
* **Spring Cloud Eureka** (Service Discovery)
* **Spring Cloud Gateway** (API Gateway y enrutamiento)
* **RabbitMQ 3-management** (Intercambio de mensajes y eventos por tópicos)
* **MySQL 8.0** (Persistencia relacional independiente por servicio)
* **Docker & Docker Compose** (Containerización y orquestación local)
* **Kubernetes (K8s)** (Orquestación enterprise de contenedores y servicios)

### 🎨 Front-End
* **Angular 17+** (Framework SPA reactivo)
* **TypeScript & RxJS** (Tipado estático y programación reactiva)
* **Nginx** (Servidor web para producción y contenedorización del frontend)

---

## 🔒 5. Seguridad y Control de Accesos
* **Autenticación Basada en Tokens:** Validación centralizada de credenciales.
* **Control de Acceso por Roles (RBAC):** Restricción de endpoints según perfil de usuario.
* **Filtros en API Gateway:** Intercepción de solicitudes no autorizadas antes de alcanzar los microservicios internos.

---

## 👥 6. Roles y Permisos

| Rol | Permisos y Accesos en el Sistema |
| :--- | :--- |
| **ADMIN** | Gestión total de usuarios, control de inventario, visualización de reportes de facturación y configuración del sistema. |
| **MESERO** | Creación y actualización de pedidos de clientes, gestión de mesas y visualización del catálogo de platos. |
| **COCINA** | Recepción de órdenes en tiempo real mediante eventos, actualización del estado de preparación de los platos. |
| **CAJERO** | Procesamiento de pagos, emisión de comprobantes/boletas y cierre de caja. |
| **DELIVERY** | Gestión y seguimiento de entregas a domicilio asignadas. |

---

## 🔄 7. Explicación Detallada del Flujo del Sistema

1. **Autenticación:** El usuario ingresa sus credenciales en el Frontend Angular, las cuales viajan a través del **API Gateway** hacia el `Auth-Service`. Si son correctas, se emite una sesión/token de acceso.
2. **Catálogo y Órdenes:** El mesero consulta los platos disponibles (consumiendo el `Catalog-Service`) y registra un nuevo pedido a través del `Order-Service`.
3. **Comunicación Asincrónica (RabbitMQ):** Al registrarse una orden exitosa, el `Order-Service` publica un evento estructurado hacia el Exchange `restaurante.topic`. 
4. **Procesamiento Paralelo:** Los servicios suscriptores (como `Billing-Service` para generar comprobantes y `Operations-Service` para notificar áreas de cocina/mesas) recogen el mensaje de forma asíncrona sin bloquear la respuesta HTTP inicial del pedido.

---

## 📦 8. Funcionalidades Principales
- Gestión completa de catálogos y menús gastronómicos.
- Toma, seguimiento y actualización del estado de pedidos.
- Generación automatizada de comprobantes de pago y boletas.
- Administración y control de estado de mesas en el local.
- Panel de control responsivo e interactivo en Angular.

---

## 🛡️ 9. Buenas Prácticas Aplicadas
- **Principio de Responsabilidad Única (SRP):** Microservicios estrictamente acotados a un dominio de negocio.
- **DTO Pattern:** Desacoplamiento de las entidades de base de datos respecto a los objetos transferidos por la API.
- **Manejo Centralizado de Excepciones:** Respuestas HTTP estandarizadas mediante `@ControllerAdvice`.
- **Validaciones Declarativas:** Uso estricto de Bean Validation (`@Valid`, `@NotNull`, `@Size`).
- **Inyección de Dependencias:** Gestión mediante el contenedor IoC de Spring.

---

## ⚙️ 10. Configuración y Ejecución

### Requisitos Previos
- Docker Desktop instalado y en ejecución (con Kubernetes habilitado opcionalmente).
- Git clonado en el equipo.

---

### 🐳 Opción A: Despliegue con Docker Compose (Entorno Local)
1. Clonar el repositorio y situarse en la raíz del proyecto.
2. Levantar todo el ecosistema integrado (Bases de datos, RabbitMQ, Eureka, Microservicios y Frontend):
   ```bash
   docker compose up -d --build
   ```
3. **URLs de Acceso:**
   - **Frontend (Angular):** `http://localhost:4200`
   - **API Gateway:** `http://localhost:8081`
   - **Eureka Dashboard:** `http://localhost:8761`
   - **RabbitMQ Management:** `http://localhost:15672` (Credenciales: `guest` / `guest`)

---

### ☸️ Opción B: Despliegue en Kubernetes (K8s)
El proyecto incluye manifiestos listos para su orquestación local:
1. Asegúrate de habilitar Kubernetes en los ajustes de Docker Desktop.
2. Aplica los manifiestos ubicados en la carpeta `k8s/`:
   ```bash
   kubectl apply -f k8s/
   ```
3. **Comandos de Verificación en Kubernetes:**
   - Comprobar que los Pods se encuentran ejecutándose correctamente (`Running`):
     ```bash
     kubectl get pods
     ```
   - Comprobar los servicios expuestos y sus puertos internos/externos:
     ```bash
     kubectl get services
     ```
   - Visualizar los logs de un microservicio específico en busca de anomalías:
     ```bash
     kubectl logs deployment/billing-service
     ```
4. **Acceso a la aplicación en Kubernetes:**
   - **Frontend expuesto mediante NodePort:** `http://localhost:30080`
   - **API Gateway expuesto:** `http://localhost:30081`

---

## 📊 11. Monitoreo y Operación
Para entornos productivos o revisiones de rendimiento locales, el sistema cuenta con:
- **Monitoreo Nativo de K8s:** Comandos de supervisión de recursos mediante `kubectl top pods` y seguimiento de estados en vivo con `kubectl get pods -w`.
- **Inspección de Tráfico Asíncrono:** Panel de gestión en vivo de RabbitMQ para auditoría de colas, exchanges y tasas de mensajes procesados.

---

## 🚀 12. Mejoras Futuras
- **Observabilidad Avanzada:** Integración de **Prometheus** para recolección de métricas de micrometer en Spring Boot y **Grafana** para visualización de dashboards de rendimiento en tiempo real.
- **Service Mesh:** Implementación de Istio para control avanzado de tráfico mTLS y seguridad entre microservicios.
- **Pruebas de Carga:** Simulación de concurrencia masiva con JMeter o k6 para evaluar el comportamiento del bus de eventos bajo alta presión.

---

## 👨‍💻 13. Autor
**Nick Fernando Rodriguez Contreras**  
*Estudiante y Autodidacta de la carrera de Computación e Informática.*  
🧑🏽‍💻😎✌️