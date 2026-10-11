/**
 * 🔧 CONFIGURACIÓN CENTRAL DE API GATEWAY
 * 
 * Este archivo define la URL base del API Gateway que se comunica
 * con todos los microservicios registrados en Eureka Server.
 * 
 * Flujo: Frontend → API Gateway (8081) → Eureka Server → Microservicios
 */

// URL base del API Gateway
export const API_GATEWAY_URL = 'http://localhost:8081';

// Rutas de los microservicios a través del API Gateway
export const API_ROUTES = {
  // 🔐 Autenticación y Usuarios
  AUTH: `${API_GATEWAY_URL}/api/auth`,
  USERS: `${API_GATEWAY_URL}/api/usuarios`,
  
  // 📦 Catálogo (Comidas y Categorías)
  COMIDAS: `${API_GATEWAY_URL}/api/comidas`,
  CATEGORIAS: `${API_GATEWAY_URL}/api/categorias`,
  
  // 👥 Clientes
  CLIENTES: `${API_GATEWAY_URL}/api/clientes`,
  
  // 🍽️ Mesas (Operations)
  MESAS: `${API_GATEWAY_URL}/api/mesas`,
  
  // 📋 Pedidos
  PEDIDOS: `${API_GATEWAY_URL}/api/pedidos`,
  
  // 💰 Comprobantes de Pago (Facturación)
  COMPROBANTES: `${API_GATEWAY_URL}/api/comprobantes`
};
