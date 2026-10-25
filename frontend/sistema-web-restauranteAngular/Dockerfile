# --- ETAPA 1: Construir la aplicación de Angular ---
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build -- --configuration production

# --- ETAPA 2: Servir la app con Nginx ---
FROM nginx:alpine

# 1. Limpiamos la carpeta por defecto de Nginx
RUN rm -rf /usr/share/nginx/html/*

# 2. Copiamos los archivos directamente desde la salida de tu build moderno
COPY --from=build /app/dist/sistema-web-restaurante/browser /usr/share/nginx/html

# 3. Copiamos nuestra configuración de Nginx para soportar rutas de Angular
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 4. Ajustamos permisos
RUN chmod -R 755 /usr/share/nginx/html && \
    chown -R nginx:nginx /usr/share/nginx/html

EXPOSE 80