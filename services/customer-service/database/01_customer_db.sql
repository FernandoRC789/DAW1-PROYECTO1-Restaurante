CREATE DATABASE IF NOT EXISTS customer_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE customer_db;

CREATE TABLE IF NOT EXISTS tb_cliente (
    id_cliente BIGINT NOT NULL AUTO_INCREMENT,
    nom_cliente VARCHAR(255) NOT NULL,
    doc_cliente VARCHAR(20) NOT NULL,
    tipo_documento VARCHAR(20) NOT NULL,
    dir_cliente VARCHAR(150),
    correo_cliente VARCHAR(100),
    tel_cliente VARCHAR(15),
    PRIMARY KEY (id_cliente),
    UNIQUE KEY uk_tb_cliente_doc_cliente (doc_cliente)
);

-- =========================================================
-- 3 DATOS DE PRUEBA
-- =========================================================

INSERT INTO tb_cliente
(nom_cliente, doc_cliente, tipo_documento, dir_cliente, correo_cliente, tel_cliente)
VALUES
('Juan Perez', '76543210', 'DNI', 'Av. Arequipa 123', 'juan.perez@gmail.com', '999111222'),
('Maria Lopez', '70123456', 'DNI', 'Jr. de la Union 456', 'maria.lopez@gmail.com', '988222333'),
('Restaurante Demo SAC', '20601234567', 'RUC', 'Av. Javier Prado 789', 'ventas@restaurantdemo.com', '977333444')
ON DUPLICATE KEY UPDATE
nom_cliente = VALUES(nom_cliente),
tipo_documento = VALUES(tipo_documento),
dir_cliente = VALUES(dir_cliente),
correo_cliente = VALUES(correo_cliente),
tel_cliente = VALUES(tel_cliente);

SELECT * FROM tb_cliente;
