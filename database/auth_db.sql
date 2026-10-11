#DEV/AUTH SCRIPT
use auth_db;

# 1. INSERTANDO REGISTROS DE ROLES (IDs del 1 al 5):
INSERT INTO tb_roles (nombre) VALUES 
("ADMIN"),     -- ID 1
("MESERO"),    -- ID 2
("COCINA"),    -- ID 3
("CAJERO"),    -- ID 4
("DELIVERY");  -- ID 5

# 2. USUARIOS DEL PERSONAL:
INSERT INTO tb_users (username, password)
VALUES ("admin_nick","$2a$12$a33yoKSM79eITckbsFEaouEa3gc6bhfz4KJlD92z/ZlXbZMRJgGzK"),#admin123
("mesero_nick","$2a$12$rtGCeylqDxXJSTCToMaqUOHIzovZUHSZV9QO8aZvJgwgIG8QRHvl."),#mesero123
("cocina_nick","$2a$12$AzizELua6vJVXdwMI3chQ.RzZdVolLPJiDD/JNKiXZFtum2GXL1w6"),#cocina123
("cajero_nick","$2a$12$rWvpAttdalQd8mlkTJgO..6D4NO6j9xBd.KL2XKYoDVBlBbDtqehC"),#cajero123
("delivery_nick","$2a$12$RpQE2VlS7YFiuu6VgYbI/ONgfHrvOUyc9osELvG44bmWM7TmxKTfa");#delivery123

#https://bcrypt-generator.com/

# 3. ASIGNANDO ROLES:
INSERT INTO tb_users_roles (usuario_id, rol_id) VALUES 
(1, 1),  # admin_nick -> ADMIN
(2, 2),  # mesero_nick -> MESERO
(3, 3),  # cocina_nick -> COCINA
(4, 4),  # cajero_nick -> CAJERO
(5, 5);  # delivery_nick -> DELIVERY

# 4. VERIFICACIÓN:
SELECT * FROM tb_users;
SELECT * FROM tb_roles;
SELECT * FROM tb_users_roles;