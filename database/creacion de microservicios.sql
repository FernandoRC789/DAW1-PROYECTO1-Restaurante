-- ==========================================================
-- SCRIPT DE CREACIÓN DE BASES DE DATOS - MICROSERVICIOS
-- ==========================================================
-- 1. Base de datos para el microservicio de Autenticación y Credenciales
CREATE DATABASE IF NOT EXISTS auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. Base de datos para el microservicio de Catálogo (Comidas, Platos y Menú)
CREATE DATABASE IF NOT EXISTS catalog_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. Base de datos para el microservicio de Pedidos (Órdenes y Detalles de Pedido)
CREATE DATABASE IF NOT EXISTS order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. Base de datos para el microservicio de Operaciones (Gestión de Mesas y Estado del Local)
CREATE DATABASE IF NOT EXISTS operations_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 5. Base de datos para el microservicio de Facturación (Comprobantes, Boletas y Pagos)
CREATE DATABASE IF NOT EXISTS billing_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 6. Base de datos para el microservicio de Clientes
CREATE DATABASE IF NOT EXISTS customer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;