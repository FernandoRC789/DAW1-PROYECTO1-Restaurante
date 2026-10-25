# Guía Definitiva de Despliegue y Arquitectura: Sistema de Restaurante (Microservicios)

Esta guía detalla los pasos exactos para configurar, compilar, empaquetar y desplegar el sistema completo de microservicios (Backend en Spring Boot, Frontend en Angular con Nginx, MySQL, RabbitMQ, Eureka y API Gateway) tanto en **Docker Compose** como en **Kubernetes**.

---

## 📋 1. Requisitos Previos e Instalación en la Máquina del Profesor
Para ejecutar este proyecto sin excepciones, asegúrate de tener instalado en el equipo:
- **Docker Desktop** (con Kubernetes habilitado en las opciones).
- **Git** (para clonar el repositorio).
- **MySQL Workbench** (opcional, para verificación de bases de datos).

---

## 🛠️ 2. Estructura del Proyecto y Manifiestos
Asegúrate de que la estructura de tu proyecto contenga la carpeta `k8s/` en la raíz con los archivos YAML de Kubernetes y tu archivo `docker-compose.yml`.

---

## 🐳 3. Generación de Imágenes Docker desde Cero (Crucial si no se tienen las imágenes previas)

Dado que las imágenes de los microservicios y del frontend deben generarse localmente a partir de los códigos fuente, ejecuta los siguientes comandos desde la **raíz del proyecto** (`C:\github\DAW1-PROYECTO1-Restaurante\`):

### A. Construir las imágenes del Backend (Microservicios Spring Boot)
Si tus microservicios cuentan con un `Dockerfile` en cada carpeta correspondiente o utilizas Maven/Gradle con empaquetado Docker, compila y construye las imágenes ejecutando:

```bash
docker build -t auth-service:latest ./auth-service
docker build -t catalog-service:latest ./catalog-service
docker build -t order-service:latest ./order-service
docker build -t operations-service:latest ./operations-service
docker build -t billing-service:latest ./billing-service
docker build -t customer-service:latest ./customer-service
docker build -t eureka-server:latest ./eureka-server
docker build -t api-gateway:latest ./api-gateway
```

### B. Construir la imagen del Frontend (Angular 17+ con Nginx)
Navega a la carpeta del frontend y construye la imagen usando el `Dockerfile` y el `nginx.conf`:
```bash
cd frontend/sistema-web-restauranteAngular
docker build -t frontend-app:latest .
cd ../../
```

---

## 🚀 4. Opción A: Despliegue con Docker Compose (Entorno Local)

Para levantar todo el ecosistema de forma automatizada mediante Docker Compose asegurando la conectividad de la red y las bases de datos:

1. Apagar cualquier contenedor previo:
   ```bash
   docker compose down
   ```
2. Levantar todos los servicios en segundo plano:
   ```bash
   docker compose up -d --build
   ```
3. **Verificación:**
   - **Frontend:** `http://localhost:4200`
   - **API Gateway:** `http://localhost:8081`
   - **RabbitMQ Management:** `http://localhost:15672` (Credenciales: `guest` / `guest`)
   - **Eureka Dashboard:** `http://localhost:8761`

---

## ☸️ 5. Opción B: Despliegue con Kubernetes (Clúster Local)

Si se desea migrar y probar la orquestación enterprise con Kubernetes en Docker Desktop:

1. Asegúrate de que Kubernetes esté activo en Docker Desktop.
2. Aplica todos los manifiestos YAML ubicados en la carpeta `k8s/`:
   ```bash
   kubectl apply -f k8s/
   ```
3. Verifica que los Pods y Servicios estén corriendo correctamente:
   ```bash
   kubectl get pods
   kubectl get services
   ```
4. Accede al frontend expuesto por NodePort:
   `http://localhost:30080`

---

## 🔍 6. Comprobaciones y Solución de Problemas Comunes

- **Verificar colas y eventos en RabbitMQ:** Ingresa a `http://localhost:15672`, navega al exchange `restaurante.topic` o a la pestaña *Queues* para observar el flujo de mensajes asíncronos en tiempo real.
- **Revisar logs de un microservicio específico en Docker:**
  ```bash
  docker logs billing-service -f
  ```
- **Revisar logs en Kubernetes:**
  ```bash
  kubectl logs deployment/billing-service