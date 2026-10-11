#DEV/catalog
use catalog_db;

#insertando registros de la tabla categorias
INSERT INTO tb_categoria (nom_cat) VALUES
('Entradas'),
('Platos de Fondo'),
('Postres'),
('Bebidas'),
('Comida Rápida'),
('Parrillas'),
('Mariscos'),
('Sopas'),
('Ensaladas'),
('Desayunos');

#insertando registros de la tabla comidas:
INSERT INTO tb_comida (nom_comida, des_comida, cat_id, pre_comida, disponible) VALUES

-- Entradas (1)
('Tequeños', 'Palitos fritos con queso', 1, 12.00, true),
('Causa Limeña', 'Papa amarilla con pollo', 1, 15.00, true),
('Papa a la Huancaína', 'Papa con salsa de queso', 1, 10.00, true),
('Anticuchos', 'Corazón de res a la parrilla', 1, 18.00, true),
('Yuca Frita', 'Yuca crocante', 1, 9.00, false),

-- Platos de Fondo (2)
('Lomo Saltado', 'Carne salteada con papas', 2, 28.00, true),
('Arroz con Pollo', 'Arroz verde con pollo', 2, 22.00, true),
('Ají de Gallina', 'Crema de ají con pollo', 2, 20.00, true),
('Seco de Carne', 'Carne guisada con frejoles', 2, 24.00, true),
('Tallarines Rojos', 'Pasta con salsa de tomate', 2, 18.00, false),

-- Postres (3)
('Tres Leches', 'Pastel húmedo dulce', 3, 12.00, true),
('Arroz con Leche', 'Postre tradicional', 3, 8.00, true),
('Mazamorra Morada', 'Postre de maíz morado', 3, 8.00, true),
('Helado', 'Helado artesanal', 3, 7.00, true),
('Picarones', 'Dulce frito con miel', 3, 10.00, false),

-- Bebidas (4)
('Inca Kola', 'Gaseosa peruana', 4, 5.00, true),
('Coca Cola', 'Gaseosa clásica', 4, 5.00, true),
('Chicha Morada', 'Bebida tradicional', 4, 6.00, true),
('Limonada', 'Refresco natural', 4, 6.00, false),
('Agua Mineral', 'Agua embotellada', 4, 3.00, true),

-- Comida Rápida (5)
('Hamburguesa', 'Hamburguesa con papas', 5, 15.00, true),
('Hot Dog', 'Pan con salchicha', 5, 10.00, true),
('Pizza Personal', 'Pizza individual', 5, 18.00, true),
('Salchipapa', 'Papas con salchicha', 5, 12.00, true),
('Nuggets', 'Pollo frito', 5, 14.00, false),

-- Parrillas (6)
('Pollo a la Brasa', 'Pollo con papas', 6, 30.00, true),
('Chuleta', 'Carne de cerdo', 6, 25.00, true),
('Bife', 'Carne de res', 6, 35.00, true),
('Costillas BBQ', 'Costillas con salsa', 6, 40.00, true),
('Parrilla Mixta', 'Variedad de carnes', 6, 50.00, false),

-- Mariscos (7)
('Ceviche', 'Pescado con limón', 7, 28.00, true),
('Arroz con Mariscos', 'Arroz con mariscos mixtos', 7, 30.00, true),
('Jalea', 'Mariscos fritos', 7, 35.00, true),
('Chupe de Camarones', 'Sopa espesa', 7, 32.00, true),
('Pulpo a la Parrilla', 'Pulpo grillado', 7, 38.00, false),

-- Sopas (8)
('Sopa de Pollo', 'Caldo con pollo', 8, 12.00, true),
('Caldo de Gallina', 'Sopa tradicional', 8, 15.00, true),
('Sopa Criolla', 'Sopa con carne y pasta', 8, 14.00, true),
('Menestrón', 'Sopa con verduras', 8, 13.00, true),
('Crema de Verduras', 'Sopa cremosa', 8, 11.00, false),

-- Ensaladas (9)
('Ensalada César', 'Lechuga con pollo', 9, 14.00, true),
('Ensalada Mixta', 'Verduras variadas', 9, 10.00, true),
('Ensalada de Palta', 'Palta con verduras', 9, 12.00, true),
('Ensalada de Quinua', 'Quinua saludable', 9, 13.00, true),
('Ensalada Caprese', 'Tomate y queso', 9, 15.00, false),

-- Desayunos (10)
('Pan con Pollo', 'Sandwich de pollo', 10, 8.00, true),
('Pan con Jamón', 'Sandwich simple', 10, 6.00, true),
('Huevos Revueltos', 'Huevos con pan', 10, 7.00, true),
('Café', 'Café caliente', 10, 4.00, true),
('Chocolate Caliente', 'Bebida caliente', 10, 6.00, false);